package com.sslmo.api.v1.users.models

import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable

@Serializable
data class EmailLoginRequest(
	@field:Schema(required = true) val email: String,
	@field:Schema(required = true) val password: String,
)


@Serializable
data class SocialLoginRequest(
	@field:Schema(required = true, name = "social_id") val socialId: String
)