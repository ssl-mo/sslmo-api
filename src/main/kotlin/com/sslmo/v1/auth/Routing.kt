package com.sslmo.v1.auth

import com.sslmo.database.DatabaseFactory
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.authRouting(
    group: String,
) {
    val auth = "$group/auth"

    routing {
        signIn(auth)
    }
}
