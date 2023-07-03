package com.sslmo.utils

import com.sslmo.models.AppMode
import io.ktor.server.config.*

fun ApplicationConfig?.getAppMode(): AppMode {
    return AppMode.valueOf(this?.propertyOrNull("app.mode")?.getString() ?: "PROD")
}

fun ApplicationConfig?.getAppName(): String {
    return this?.propertyOrNull("app.name")?.getString() ?: "sslmo-api-prod"
}

fun ApplicationConfig?.getAppHost(): String {
    return this?.propertyOrNull("app.host")?.getString() ?: "https://prod-sslmo-api.fly.dev"
}

fun ApplicationConfig?.getAppKeyHeader(): String {
    return this?.propertyOrNull("app.key.header")?.getString() ?: "keyHeader"
}

fun ApplicationConfig?.getAppKeyValue(): String {
    return this?.propertyOrNull("app.key.value")?.getString() ?: "keyValue"
}

fun ApplicationConfig?.getAccessJWTSecret(): String {
    return this?.propertyOrNull("jwt.secret.access")?.getString() ?: "secret"
}

fun ApplicationConfig?.getRefreshJWTSecret(): String {
    return this?.propertyOrNull("jwt.secret.refresh")?.getString() ?: "secret"
}

fun ApplicationConfig?.getDBUrl(): String {
    return this?.propertyOrNull("database.url")?.getString() ?: "jdbc:mysql://localhost:3306/sslmo"
}

fun ApplicationConfig?.getDBUser(): String {
    return this?.propertyOrNull("database.user")?.getString() ?: "root"
}

fun ApplicationConfig?.getDBPassword(): String {
    return this?.propertyOrNull("database.password")?.getString() ?: "1234"
}

fun ApplicationConfig?.getSwaggerUser(): String {
    return this?.propertyOrNull("swagger.name")?.getString() ?: "test"
}

fun ApplicationConfig?.getSwaggerPassword(): String {
    return this?.propertyOrNull("swagger.password")?.getString() ?: "1234"
}