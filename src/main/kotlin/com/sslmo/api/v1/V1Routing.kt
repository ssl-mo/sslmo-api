package com.sslmo.api.v1

import com.sslmo.api.v1.auth.authRouting
import com.sslmo.api.v1.users.usersRouting
import io.ktor.server.routing.*

fun Route.v1Routing() {
    route("/v1") {
        authRouting()
        usersRouting()
    }
}