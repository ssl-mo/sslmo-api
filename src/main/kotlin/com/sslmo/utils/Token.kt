package com.sslmo.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sslmo.database.User
import io.ktor.server.config.*
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun User.getAccessToken(config: ApplicationConfig): String {
    val secret = config.getAccessJWTSecret()

    return JWT.create()
        .withClaim("uuid", uuid.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 30))
        .sign(Algorithm.HMAC256(secret))
}

fun User.getRefreshToken(config: ApplicationConfig): String {
    val secret = config.getRefreshJWTSecret()

    return JWT.create()
        .withClaim("uuid", uuid.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 14))
        .sign(Algorithm.HMAC256(secret))
}


@Serializable
data class Token(
    val accessToken: String,
    val refreshToken: String
) {

    companion object {
        fun generateToken(config: ApplicationConfig, user: User): Token {

            val jwtSecret = config.getAccessJWTSecret()
            val jwtAudience = config.getAppName()
            val jwtIssuer = config.getAppHost()
            var expiredAt = LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.UTC)


            val accessToken =
                JWT.create()
                    .withIssuer(jwtIssuer)
                    .withAudience(jwtAudience)
                    .withSubject(user.uuid.toString())
                    .withExpiresAt(expiredAt)
                    .sign(Algorithm.HMAC256(jwtSecret))

            expiredAt = LocalDateTime.now().plusDays(14).toInstant(ZoneOffset.UTC)

            val refreshToken =
                JWT.create()
                    .withIssuer(jwtIssuer)
                    .withAudience(jwtAudience)
                    .withSubject(user.uuid.toString())
                    .withExpiresAt(expiredAt)
                    .sign(Algorithm.HMAC256(jwtSecret))


            return Token(accessToken, refreshToken)
        }
    }
}