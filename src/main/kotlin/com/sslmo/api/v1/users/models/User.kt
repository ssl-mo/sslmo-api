package com.sslmo.api.v1.users.models

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sslmo.models.SignType
import com.sslmo.models.TokenType
import com.sslmo.utils.Token
import com.sslmo.utils.getAccessJWTSecret
import com.sslmo.utils.getAppHost
import com.sslmo.utils.getAppName
import io.ktor.server.config.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Serializable
data class User(
	val id: Int,

	@Contextual
	val uuid: UUID,
	val type: SignType,
	val socialId: String?,
	val email: String,

	@Transient
	val password: String? = null,
	val nickname: String,
	val notification: Boolean,
	val active: Boolean,

	@Contextual
	val inActiveAt: LocalDate?,

	@Contextual
	val createdAt: LocalDate,

	@Contextual
	val updatedAt: LocalDate?,

	val siCode: Long,
	val guCode: Long,
	val dongCode: Long
) {


	fun generateToken(config: ApplicationConfig): Token {

		val jwtSecret = config.getAccessJWTSecret()
		val jwtAudience = config.getAppName()
		val jwtIssuer = config.getAppHost()
		var expiredAt = LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.UTC)


		val accessToken =
			JWT.create()
				.withIssuer(jwtIssuer)
				.withAudience(jwtAudience)
				.withSubject(uuid.toString())
				.withClaim("type", TokenType.ACCESS.name)
				.withExpiresAt(expiredAt)
				.sign(Algorithm.HMAC256(jwtSecret))

		expiredAt = LocalDateTime.now().plusDays(14).toInstant(ZoneOffset.UTC)

		val refreshToken =
			JWT.create()
				.withIssuer(jwtIssuer)
				.withAudience(jwtAudience)
				.withSubject(uuid.toString())
				.withClaim("type", TokenType.ACCESS.name)
				.withExpiresAt(expiredAt)
				.sign(Algorithm.HMAC256(jwtSecret))


		return Token(accessToken, refreshToken)
	}
}
