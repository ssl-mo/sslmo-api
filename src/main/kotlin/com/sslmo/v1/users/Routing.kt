package com.sslmo.v1.users

import DefaultResponse
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.usersRouting(
    group: String,
) {
    val users = "$group/users"

    routing {
        get(users) {
            call.respondText("sslmo-api")
        }
    }
}
