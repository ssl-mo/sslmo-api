package com.sslmo

import DefaultResponse
import com.sslmo.v1.v1Routing
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("sslmo-api")
        }

        get("/ping") {
            call.respond(DefaultResponse(message = "pong"))
        }
    }

    v1Routing()
}
