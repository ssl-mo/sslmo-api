package com.sslmo.models.user

import com.sslmo.models.SignType
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable


abstract class BaseRegisterRequest {
	abstract val email: String
	abstract val nickName: String
}

@Serializable
data class EmailRegisterRequest(
	override val email: String,
	val password: String,
	override val nickName: String
) : BaseRegisterRequest()


@Serializable
data class SocialRegisterRequest(
	@field:Schema(required = true, name = "social_id")
	val socialId: String,
	val type: SignType,
	override val email: String,
	override val nickName: String
) : BaseRegisterRequest()

