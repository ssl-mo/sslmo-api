package com.sslmo.v1.auth

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.authRouting(
    group: String,
) {
    val auth = "$group/auth"

    routing {
        signIn(auth)
        signCheck(auth)
    }
}
