package com.sslmo.api.v1.users

import Response
import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.database.DatabaseFactory
import com.sslmo.database.DatabaseFactory.dbQuery
import com.sslmo.database.tables.Users
import com.sslmo.database.tables.users
import com.sslmo.models.SignType
import com.sslmo.models.user.EmailLoginRequest
import com.sslmo.models.user.EmailRegisterRequest
import com.sslmo.models.user.SocialRegisterRequest
import com.sslmo.utils.setCookie
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find

fun Route.register() {

    route("/register") {


        // 이메일로 가입
        post("/email", {
            tags = listOf("User")
            protected = true
            summary = "회원 가입"
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
        })
        {
            val request = call.receive<EmailRegisterRequest>()


            val database = DatabaseFactory.connect()

            database.users.find { it.email eq request.email }?.let {
                call.respond(HttpStatusCode.Conflict, Response.Error("이미 가입된 이메일입니다.", "가입에 실패했습니다."))
                return@post
            } ?: run {

                database.users.find { it.nickname eq request.nickName }?.let {
                    call.respond(HttpStatusCode.Conflict, Response.Error("이미 사용중인 닉네임입니다.", "다시 시도해주세요"))
                    return@post
                } ?: run {


                    val hashedPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

                    val id = dbQuery { database ->
                        database.insertAndGenerateKey(Users) {
                            set(it.email, request.email)
                            set(it.password, hashedPassword)
                            set(it.nickname, request.nickName)
                            set(it.type, SignType.EMAIL)
                        }
                    }.let {
                        it as Int
                    }


                    database.users.find { it.id eq id }!!.let { user ->
                        val token = user.generateToken(application.environment.config)
                        this.setCookie(token.accessToken, token.refreshToken)
                        call.respond(HttpStatusCode.OK, Response.Success(user, "회원가입에 성공했습니다."))

                    }
                }
            }
        }

        // 소셜 로그인(가입)
        post("/social", {
            tags = listOf("User")
            protected = true
            summary = "소셜 가입"
            request {
                body<SocialRegisterRequest>()
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
            val request = call.receive<SocialRegisterRequest>()
            val database = DatabaseFactory.connect()

            // 1. 이미 가입되어 있는 유저인지 확인
            database.users.find {
                it.socialId eq request.socialId
                it.email eq request.email
            }?.let {
                call.respond(
                    HttpStatusCode.Conflict,
                    Response.Error("이미 ${it.type}으로 가입된 이메일입니다. 로그인을 시도해주세요", "가입에 실패했습니다.")
                )
                return@post
            }


            database.users.find { it.nickname eq request.nickName }?.let {
                call.respond(HttpStatusCode.Conflict, Response.Error("이미 사용중인 닉네임입니다.", "다시 시도해주세요"))
                return@post
            }

            val user = dbQuery { database ->
                val id = database.insertAndGenerateKey(Users) {
                    set(it.email, request.email)
                    set(it.socialId, request.socialId)
                    set(it.nickname, request.nickName)
                    set(it.type, request.type)
                }.let {
                    it as Int
                }

                database.users.find { it.id eq id }!!
            }

            // 4. 토큰 발급
            val token = user.generateToken(application.environment.config)
            // 5. 쿠키에 토큰 저장
            this.setCookie(token.accessToken, token.refreshToken)
            // 6. 응답
            call.respond(HttpStatusCode.OK, Response.Success(user, "회원가입에 성공했습니다."))

        }

    }


}