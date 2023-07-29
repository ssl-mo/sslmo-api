package com.sslmo.modules

import com.sslmo.models.SignType
import com.sslmo.models.user.LoginRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<LoginRequest> { req ->
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
}