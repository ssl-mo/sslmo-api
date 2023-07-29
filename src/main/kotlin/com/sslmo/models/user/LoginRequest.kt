package com.sslmo.models.user

import com.sslmo.models.SignType
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @field:Schema(required = true) val type: SignType,
    @field:Schema(required = true) val email: String,

    val socialId: String? = null,
    val password: String? = null,
    val token: String? = null,
)