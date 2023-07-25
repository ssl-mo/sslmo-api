package com.sslmo.modules

import com.sslmo.models.AppMode
import com.sslmo.utils.getAppMode
import com.sslmo.utils.getSwaggerPassword
import com.sslmo.utils.getSwaggerUser
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuthorization() {
    if (environment.config.getAppMode() != AppMode.PROD) {
        install(Authentication) {
            basic("auth-basic") {
                validate { credentials ->
                    if (credentials.name == application.environment.config.getSwaggerUser() && credentials.password == application.environment.config.getSwaggerPassword()) {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
            }
        }

        // Swagger UI
        configureSwaggerUI()
    }
}