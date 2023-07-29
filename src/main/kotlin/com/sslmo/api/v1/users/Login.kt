package com.sslmo.api.v1.users

import Response
import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.database.DatabaseFactory
import com.sslmo.database.tables.users
import com.sslmo.models.SignType
import com.sslmo.models.user.LoginRequest
import com.sslmo.models.user.LoginResponse
import com.sslmo.utils.Token
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.find

fun Route.login() {

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


                call.respond(
                    HttpStatusCode.OK,
                    Response.Success(
                        LoginResponse(user, Token.generateToken(config, user)),
                        "로그인에 성공하였습니다."
                    )
                )


            }

            else -> {}
        }
    }
}