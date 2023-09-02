package com.sslmo.api.v1.users.models

import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable


@Serializable
data class PasswordChangeRequest(@field:Schema(name = "new_password") val newPassword: String)
