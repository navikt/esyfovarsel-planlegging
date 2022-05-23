package no.nav.syfo.api.system

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

val urlPathJobTrigger = "/api/job/trigger"

fun Route.registerJobTriggerApi() {
    accept(ContentType.Application.Json) {
        post(urlPathJobTrigger) {
            call.respond(0)
        }
    }
}
