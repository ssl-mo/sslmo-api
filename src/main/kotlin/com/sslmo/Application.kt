package com.sslmo

import DefaultResponse
import com.sslmo.database.DatabaseFactory
import com.sslmo.models.SignType
import com.sslmo.plugins.AuthorizedRouteSelector
import com.sslmo.v1.auth.SignInRequest
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.forwardedheaders.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment.config)

    install(XForwardedHeaders)

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    install(RequestValidation) {
        validate<SignInRequest> { req ->
            when (req.type) {
                SignType.EMAIL -> if (req.email.isEmpty() && req.password?.isEmpty() == true) ValidationResult.Invalid(
                    "email"
                ) else ValidationResult.Valid

                else -> if (req.email.isEmpty() && req.socialId?.isEmpty() == true) ValidationResult.Invalid(
                    "social"
                ) else ValidationResult.Valid
            }
        }
    }

    install(StatusPages) {
        exception<BadRequestException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest, DefaultResponse(message = "bad_request"))
        }
        exception<RequestValidationException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest, DefaultResponse(message = "bad_request"))
        }
    }

    configureRouting()

    install(SwaggerUI) {
        ignoredRouteSelectors = listOf(AuthorizedRouteSelector::class)
        swagger {
            swaggerUrl = "swagger"
            forwardRoot = true
        }
        info {
            title = "SSLMO API"
            version = "latest"
        }
        server {
            url = "http://localhost:8080"
            description = "Development Server"
        }
    }
}