package com.sslmo.api.v1.users.models

import com.sslmo.models.SignType
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable


sealed class BaseRegisterRequest {
	abstract val email: String
	abstract val nickName: String
	abstract val siCode: Long
	abstract val guCode: Long
	abstract val dongCode: Long

}

@Serializable
data class EmailRegisterRequest(
	override val email: String,
	val password: String,
	override val nickName: String,
	override val siCode: Long,
	override val guCode: Long,
	override val dongCode: Long
) : BaseRegisterRequest()


@Serializable
data class SocialRegisterRequest(
	@field:Schema(required = true, name = "social_id")
	val socialId: String,
	val type: SignType,
	override val email: String,
	override val nickName: String,
	override val siCode: Long,
	override val guCode: Long,
	override val dongCode: Long

) : BaseRegisterRequest()

