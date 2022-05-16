package no.nav.syfo

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.asCoroutineDispatcher
import no.nav.syfo.api.registerNaisApi
import no.nav.syfo.auth.*
import no.nav.syfo.consumer.*
import no.nav.syfo.consumer.syfosmregister.SykmeldingerConsumer
import no.nav.syfo.db.*
import no.nav.syfo.metrics.registerPrometheusApi
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

data class ApplicationState(var running: Boolean = false, var initialized: Boolean = false)

val state: ApplicationState = ApplicationState()
val backgroundTasksContext = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
lateinit var database: DatabaseInterface

fun main() {
    val env = getEnv()
    val server = embeddedServer(Netty, applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())
        database = Database(env.dbEnv)

        val azureAdTokenConsumer = AzureAdTokenConsumer(env.authEnv)

        val sykmeldingerConsumer = SykmeldingerConsumer(env.urlEnv, azureAdTokenConsumer)

        // TODO: VARSELPLANNERS + REPLANLEGGINGSSERVICE


        connector {
            port = env.appEnv.applicationPort
        }

        module {
            state.running = true

            serverModule(
                env
            )

            kafkaModule(
                env
            )
        }
    })

    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(10, 10, TimeUnit.SECONDS)
    })

    server.start(wait = false)
}


fun Application.serverModule(
    env: Environment
) {
    install(ContentNegotiation) {
        jackson {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    log.info("GRANTING ACCESS TO IAM ...")
    database.grantAccessToIAMUsers()
    log.info("ACCESS GRANTED")

    // TODO: Sett opp API (replannleggging)

    routing {
        registerPrometheusApi()
        registerNaisApi(state)
    }

    state.initialized = true
}

fun Application.kafkaModule(
    env: Environment,
) {
    // TODO: VARSELPLANNERS
}

val Application.envKind
    get() = environment.config.property("ktor.environment").getString()

fun Application.runningRemotely(block: () -> Unit) {
    if (envKind == "remote") block()
}

fun Application.runningLocally(block: () -> Unit) {
    if (envKind == "local") block()
}
