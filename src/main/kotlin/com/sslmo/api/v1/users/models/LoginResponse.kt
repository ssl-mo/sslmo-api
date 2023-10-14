package com.sslmo.api.v1.users.models

import com.sslmo.utils.Token
import kotlinx.serialization.Serializable


@Serializable
data class LoginResponse(val userModel: UserModel, val token: Token)
