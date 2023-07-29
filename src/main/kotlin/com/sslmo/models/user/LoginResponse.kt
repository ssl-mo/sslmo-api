package com.sslmo.models.user

import com.sslmo.database.User
import com.sslmo.utils.Token
import kotlinx.serialization.Serializable


@Serializable
data class LoginResponse(val user: User, val token: Token)
