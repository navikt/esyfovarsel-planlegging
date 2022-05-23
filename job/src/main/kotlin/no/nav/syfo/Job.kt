package no.nav.syfo

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

const val jobTriggerUrl = "https://esyfovarsel-planlegging.dev.intern.nav.no/api/job/trigger"

val log: Logger = LoggerFactory.getLogger("no.nav.syfo.Job")

fun httpClient(): HttpClient {
    return HttpClient(CIO) {
        expectSuccess = false
        install(JsonFeature) {
            serializer = JacksonSerializer {
                registerKotlinModule()
                registerModule(JavaTimeModule())
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }
}

fun main() {
    log.info("JOB: Kjører jobb")
    runBlocking {
        val response: HttpResponse = httpClient().post(jobTriggerUrl)

        val status = response.status
        if (status == HttpStatusCode.OK) {
            val varslerSendt = response.receive<Int>()
            log.info("Varsler sendt: $varslerSendt")
        } else {
            log.error("Feil i esyfovarsel-planlegging-job: Klarte ikke kalle trigger-API i esyfovarsel-planlegging. Fikk svar med status: $status")
        }
    }
}