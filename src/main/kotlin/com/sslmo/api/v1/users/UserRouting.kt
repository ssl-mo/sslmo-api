package com.sslmo.api.v1.users

import io.ktor.server.routing.*

fun Route.usersRouting() {
    route("/users") {
        signUp()
    }
}