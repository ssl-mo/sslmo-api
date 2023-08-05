package com.sslmo.modules

import com.sslmo.models.user.EmailLoginRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<EmailLoginRequest> { req ->

            val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")

            when {
                req.email.isEmpty() -> ValidationResult.Invalid("email을 입력해주세요")
                !emailRegex.matches(req.email) -> ValidationResult.Invalid("email을 입력해주세요")
                req.password.isEmpty() -> ValidationResult.Invalid("password를 입력해주세요")
                else -> ValidationResult.Valid
            }


        }
    }
}