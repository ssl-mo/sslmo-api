package com.sslmo.utils

import com.sslmo.models.AppMode
import io.ktor.server.application.*


fun Application.getAppMode(): AppMode {
    return AppMode.valueOf(environment.config.propertyOrNull("app.mode")?.getString() ?: "PROD")
}
