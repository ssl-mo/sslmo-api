package com.sslmo.modules

import DefaultResponse
import com.sslmo.api.v1.v1Routing
import com.sslmo.utils.getAppName
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(application.environment.config.getAppName())
        }

        get("/ping") {
            call.respond(DefaultResponse(message = "pong"))
        }

        v1Routing()
    }

}
