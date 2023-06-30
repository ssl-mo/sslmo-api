package com.sslmo.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sslmo.database.User
import io.ktor.server.config.*
import java.util.*

fun User.getAccessToken(config: ApplicationConfig): String {
    val secret = config.propertyOrNull("jwt.secret.access")?.getString() ?: "secret"

    return JWT.create()
        .withClaim("uuid", uuid.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 30))
        .sign(Algorithm.HMAC256(secret))
}

fun User.getRefreshToken(config: ApplicationConfig): String {
    val secret = config.propertyOrNull("jwt.refresh.access")?.getString() ?: "secret"

    return JWT.create()
        .withClaim("uuid", uuid.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14))
        .sign(Algorithm.HMAC256(secret))
}