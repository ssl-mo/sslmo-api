package com.sslmo.modules

import com.sslmo.api.v1.users.models.BaseRegisterRequest
import com.sslmo.api.v1.users.models.EmailLoginRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
	install(RequestValidation) {
		validate<EmailLoginRequest> { req ->
			val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")

			when {
				req.email.isEmpty() -> ValidationResult.Invalid("email")
				!emailRegex.matches(req.email) -> ValidationResult.Invalid("email")
				req.password.isEmpty() -> ValidationResult.Invalid("password")
				else -> ValidationResult.Valid
			}
		}

		validate<BaseRegisterRequest> { req ->
			val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")
			when {
				req.email.isEmpty() -> ValidationResult.Invalid("email")
				!emailRegex.matches(req.email) -> ValidationResult.Invalid("email")
				req.nickName.isEmpty() -> ValidationResult.Invalid("nick_name")
				else -> ValidationResult.Valid
			}
		}
	}
}