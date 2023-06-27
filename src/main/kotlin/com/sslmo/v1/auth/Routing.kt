package com.sslmo.v1.auth

import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

fun Application.authRouting(
    group: String,
) {
    val auth = "$group/auth"

    routing {
        post(
            "$auth/sign",
            {
                tags = listOf("Auth")
                summary = "로그인"
            }) { _ ->
            call.respondText("sign")
        }
    }
}
