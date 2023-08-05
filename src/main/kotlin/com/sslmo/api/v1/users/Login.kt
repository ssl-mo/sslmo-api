package com.sslmo.api.v1.users

import Response
import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.database.DatabaseFactory
import com.sslmo.database.tables.users
import com.sslmo.models.SignType
import com.sslmo.models.user.LoginRequest
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.slf4j.LoggerFactory

fun Route.login() {

    val logger = LoggerFactory.getLogger("Login Routing")

    post("/login", {
        tags = listOf("User")
        protected = true
        summary = "로그인"
        request {
            body<LoginRequest>()
        }
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<Response>()
            }
            HttpStatusCode.BadRequest to {
                description = "bad_request"
                body<Response>()
            }
        }
    }) {
        val request = call.receive<LoginRequest>()
        val database = DatabaseFactory.connect()
        when (request.type) {
            SignType.EMAIL -> {


                val user = database.users.find {
                    (it.email eq request.email) and (it.type eq request.type)
                }?.apply {
                    val passwordVeriyfied =
                        BCrypt.verifyer().verify(request.password!!.toCharArray(), this.password).verified

                    if (!passwordVeriyfied) {
                        call.respond(HttpStatusCode.BadRequest, Response.Error("비밀번호가 일치하지 않습니다.", "다시 시도해주세요"))
                        return@post
                    }
                } ?: run {
                    call.respond(HttpStatusCode.NotFound, Response.Error("존재하지 않는 유저입니다.", "다시 시도해주세요"))
                    return@post
                }

                val config = application.environment.config

                val token = user.generateToken(config)


                logger.debug("token: {}", token)


                // 쿠키에 토큰 저장
                call.response.cookies.append(
                    Cookie(
                        // access token
                        name = "access-token",
                        value = token.accessToken,
                        path = "/",
                        maxAge = 60 * 30,
                        secure = false,
                        httpOnly = true,
                    )
                )

                call.response.cookies.append(
                    Cookie(
                        // refresh token
                        name = "refresh-token",
                        value = token.refreshToken,
                        path = "/",
                        maxAge = 60 * 60 * 24 * 14,
                        secure = false,
                        httpOnly = true,
                    )
                )


                call.respond(
                    HttpStatusCode.OK,
                    Response.Success(
                        user,
                        "로그인에 성공하였습니다."
                    )
                )


            }

            else -> {}
        }
    }
}