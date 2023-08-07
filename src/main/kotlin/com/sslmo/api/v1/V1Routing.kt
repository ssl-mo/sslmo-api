package com.sslmo.api.v1

import com.sslmo.api.v1.users.usersRouting
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Route.v1Routing() {
    route("/v1") {
        val logger = LoggerFactory.getLogger("V1 Routing")

        usersRouting()


    }
}