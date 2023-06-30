package com.sslmo.v1.users

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
