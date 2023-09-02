package com.sslmo.api.v1.users.models

import com.sslmo.models.SignType
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable

@Serializable
data class EmailLoginRequest(
	@field:Schema(required = true) val email: String,
	@field:Schema(required = true) val password: String,
)


@Serializable
data class SocialLoginRequest(
	@field:Schema(required = true, name = "social_id") val socialId: String,
	@field:Schema(required = true) val type: SignType
) {
	init {
		assert(type != SignType.EMAIL) // SignType이 EMAIL인 경우 assert 에러를 발생
	}
}