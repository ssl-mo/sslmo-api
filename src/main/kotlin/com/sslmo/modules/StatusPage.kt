package com.sslmo.modules

import DefaultResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<BadRequestException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest, DefaultResponse(message = "bad_request"))
        }
        exception<RequestValidationException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest, DefaultResponse(message = "bad_request"))
        }
    }
}