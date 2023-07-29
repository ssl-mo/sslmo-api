package com.sslmo.models.user

import com.sslmo.models.SignType
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDate
import java.util.*

@Serializable
data class User(
    val id: Int,

    @Contextual
    val uuid: UUID,
    val type: SignType,
    val socialId: String?,
    val email: String,

    @Transient
    val password: String? = null,
    val nickname: String,
    val notification: Boolean,
    val active: Boolean,

    @Contextual
    val inActiveAt: LocalDate?,

    @Contextual
    val createdAt: LocalDate,

    @Contextual
    val updatedAt: LocalDate?,
)
