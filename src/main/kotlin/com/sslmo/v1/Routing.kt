package com.sslmo.v1

import com.sslmo.v1.auth.authRouting
import com.sslmo.v1.users.usersRouting
import io.ktor.server.application.*

fun Application.v1Routing() {
    val group = "/v1"

    authRouting(group)
    usersRouting(group)
}
