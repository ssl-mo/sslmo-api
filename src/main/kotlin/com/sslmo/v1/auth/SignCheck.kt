package com.sslmo.v1.auth

import DefaultResponse
import com.sslmo.models.AuthorizedType
import com.sslmo.plugins.authorizedRoute
import com.sslmo.plugins.getUser
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.signCheck(
    group: String,
) {
    authorizedRoute(listOf(AuthorizedType.APP, AuthorizedType.ACCESS)) {
        get("$group/sign", {
            tags = listOf("Auth")
            summary = "로그인 확인"
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<DefaultResponse>()
                }
            }
        }) { _ ->
            val user = call.getUser()

            println(user)

            call.respond(DefaultResponse(message = "success"))
        }
    }
}