package com.sslmo.models.user

import com.sslmo.models.SignType
import kotlinx.serialization.Serializable


@Serializable
data class UserRegisterRequest(
    val email: String,
    val nickname: String,
    val password: String?,
    val socialId: String?,
    val type: SignType,
)
