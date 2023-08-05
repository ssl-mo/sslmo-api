package com.sslmo.api.v1.users

import Response
import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.database.DatabaseFactory
import com.sslmo.database.tables.users
import com.sslmo.models.SignType
import com.sslmo.models.user.EmailLoginRequest
import com.sslmo.models.user.LoginResponse
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.slf4j.LoggerFactory

fun Route.login() {

    val logger = LoggerFactory.getLogger("Login Routing")


    route("/login") {

        get("/email", {
            tags = listOf("User")
            summary = "이메일 로그인"
            request {
                body<EmailLoginRequest>()
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<Response.Success<*>>()
                }
                HttpStatusCode.BadRequest to {
                    description = "bad_request"
                    body<Response.Error<*>>()
                }
            }
        }) {

            val request = call.receive<EmailLoginRequest>()

            val database = DatabaseFactory.connect()

            val user = database.users.filter {
                it.type eq SignType.EMAIL
            }.find {
                it.email eq request.email
            }


            if (user == null) {
                call.respond(HttpStatusCode.NotFound, Response.Error("존재하지 않는 유저입니다.", "로그인에 실패하였습니다."))
                return@get
            } else {
                val passwordVeriyfied =
                    BCrypt.verifyer().verify(request.password.toCharArray(), user.password).verified

                if (!passwordVeriyfied) {
                    call.respond(HttpStatusCode.Unauthorized, Response.Error("비밀번호가 일치하지 않습니다.", "로그인에 실패하였습니다."))
                    return@get
                } else {
                    val config = application.environment.config

                    val token = user.generateToken(config)

                    call.respond(HttpStatusCode.OK, Response.Success(LoginResponse(user, token), "로그인에 성공하였습니다."))
                }
            }


        }
    }

}