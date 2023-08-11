package com.sslmo.api.v1

import Response
import com.sslmo.api.v1.users.usersRouting
import com.sslmo.models.TokenType
import com.sslmo.plugins.getUser
import com.sslmo.plugins.withCookie
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Route.v1Routing() {
    route("/v1") {
        val logger = LoggerFactory.getLogger("V1 Routing")

        usersRouting()


        withCookie(TokenType.ACCESS) {

            get("/test") {

                val cookie = call.request.cookies

                logger.debug("cookies: {}", cookie)

                call.respond(HttpStatusCode.OK, Response.Success(data = call.getUser(), message = "테스트 성공"))
            }
        }


    }
}