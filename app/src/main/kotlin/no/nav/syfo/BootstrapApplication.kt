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
import kotlinx.coroutines.launch
import no.nav.syfo.api.registerNaisApi
import no.nav.syfo.api.system.registerJobTriggerApi
import no.nav.syfo.db.Database
import no.nav.syfo.db.grantAccessToIAMUsers
import no.nav.syfo.kafka.planlagte_varsler.PlanlagteVarslerKafkaConsumer
import no.nav.syfo.kafka.launchKafkaListener
import no.nav.syfo.service.PlanlagtVarselService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

data class ApplicationState(var running: Boolean = false, var initialized: Boolean = false)

val state: ApplicationState = ApplicationState()
val backgroundTasksContext = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

fun main() {
    val env = getEnv()

    val database = Database(env.dbEnv)
    val planlagtVarselService = PlanlagtVarselService(database)
    val planlagteVarslerKafkaConsumer = PlanlagteVarslerKafkaConsumer(env, planlagtVarselService)

    val server = embeddedServer(Netty, applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())

        connector {
            port = env.appEnv.applicationPort
        }

        module {
            serverModule()
            kafkaModule(planlagteVarslerKafkaConsumer)

            state.running = true
        }
    })

    database.grantAccessToIAMUsers()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop(10, 10, TimeUnit.SECONDS)
    })

    server.start(wait = false)

    state.initialized = true
}
fun Application.serverModule() {
    install(ContentNegotiation) {
        jackson {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }

    routing {
        registerNaisApi(state)
        registerJobTriggerApi()
    }
}

fun Application.kafkaModule(
    planlagteVarslerKafkaConsumer: PlanlagteVarslerKafkaConsumer
) {

    launch(backgroundTasksContext) {
        launchKafkaListener(
            state,
            planlagteVarslerKafkaConsumer
        )
    }
}
