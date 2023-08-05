package com.sslmo.models.user

import kotlinx.serialization.Serializable


@Serializable
data class EmailRegisterRequest(
    val email: String,
    val password: String,
    val nickName: String
)
