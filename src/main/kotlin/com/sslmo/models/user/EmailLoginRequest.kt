package com.sslmo.models.user

import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable

@Serializable
data class EmailLoginRequest(

    @field:Schema(required = true) val email: String,
    @field:Schema(required = true) val password: String,
)