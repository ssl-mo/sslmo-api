package com.sslmo.modules

import DefaultResponse
import Response
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
        exception<RequestValidationException> { call, e ->

            call.respond(HttpStatusCode.BadRequest, Response.Error(e.reasons, "해당 필드에서 오류가 발생했습니다."))
        }
    }
}