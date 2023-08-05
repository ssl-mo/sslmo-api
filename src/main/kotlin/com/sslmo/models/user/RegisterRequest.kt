package com.sslmo.models.user

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
abstract class LoginRequest(
    @Transient
    open val email: String = "",

    @Transient
    open val nickName: String = "",

    )

@Serializable
data class EmailRegisterRequest(
    override val email: String,
    val password: String,
    override val nickName: String
) : LoginRequest()


@Serializable
data class SocialRegisterRequest(
    val socialId: String,
    override val email: String,
    override val nickName: String
) : LoginRequest()

