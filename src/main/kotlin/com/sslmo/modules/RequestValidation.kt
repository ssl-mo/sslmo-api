package com.sslmo.modules

import com.sslmo.api.v1.auth.SignInRequest
import com.sslmo.models.SignType
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
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
}