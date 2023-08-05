package com.sslmo.api.v1.users

import Response
import com.sslmo.models.TokenType
import com.sslmo.plugins.authorizedRoute
import com.sslmo.plugins.getUser
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRouting() {
    route("/user") {

        login()

        register()


        route("/test") {

            authorizedRoute(TokenType.ACCESS) {
                get {
                    call.respond(HttpStatusCode.OK, Response.Success(data = call.getUser(), message = "테스트 성공"))

                }
            }

        }

    }
}