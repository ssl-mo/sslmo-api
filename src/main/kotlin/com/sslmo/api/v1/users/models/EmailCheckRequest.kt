package com.sslmo.api.v1.users.models

import kotlinx.serialization.Serializable


@Serializable
data class EmailCheckRequest(val email: String)
