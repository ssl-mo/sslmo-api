package com.sslmo.plugins

import DefaultResponse
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("sslmo-api")
        }

        get("/ping") {
            call.respond(DefaultResponse(message = "pong"))
        }
    }
}
