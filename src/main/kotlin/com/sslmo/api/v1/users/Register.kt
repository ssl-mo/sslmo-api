package com.sslmo.api.v1.users

import Response
import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.database.DatabaseFactory
import com.sslmo.database.DatabaseFactory.dbQuery
import com.sslmo.database.tables.Users
import com.sslmo.database.tables.users
import com.sslmo.models.AppMode
import com.sslmo.models.SignType
import com.sslmo.models.user.EmailLoginRequest
import com.sslmo.models.user.EmailRegisterRequest
import com.sslmo.models.user.SocialRegisterRequest
import com.sslmo.utils.getAppMode
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.filter
import org.ktorm.entity.find

fun Route.register() {

    route("/register") {

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
        }) {
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

                        call.response.cookies.apply {
                            append(
                                Cookie(
                                    "access-token",
                                    token.accessToken,
                                    maxAge = 60 * 60 * 24 * 7,
                                    httpOnly = true,
                                    secure = application.environment.config.getAppMode() == AppMode.PROD
                                )
                            )
                            append(
                                Cookie(
                                    "refresh-token",
                                    token.refreshToken,
                                    maxAge = 60 * 60 * 24 * 30,
                                    httpOnly = true,
                                    secure = application.environment.config.getAppMode() == AppMode.PROD
                                )
                            )
                        }


                        call.respond(HttpStatusCode.OK, Response.Success(user, "회원가입에 성공했습니다."))

                    }
                }
            }
        }



        post("/kakao", {
            tags = listOf("User")
            protected = true
            summary = "카카오 가입"
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

            database.users.filter { it.type eq SignType.KAKAO }.find { it.socialId eq request.socialId }?.let {
                call.respond(HttpStatusCode.Conflict, Response.Error("이미 가입된 카카오 계정입니다.", "가입에 실패했습니다."))
                return@post
            } ?: run {

                database.users.find { it.email eq request.email }?.let {
                    call.respond(
                        HttpStatusCode.Conflict,
                        Response.Error("해당 이메일은 소셜 로그인이 아닌 이메일로 가입되었습니다. 이메일로 로그인으로 시도해주세요", "가입에 실패했습니다.")
                    )
                    return@post
                } ?: run {

                    val user = database.users.find { it.nickname eq request.nickName }?.let {
                        call.respond(HttpStatusCode.Conflict, Response.Error("이미 사용중인 닉네임입니다.", "다시 시도해주세요"))
                        return@post
                    } ?: run {

                        val id = dbQuery { database ->
                            database.insertAndGenerateKey(Users) {
                                set(it.socialId, request.socialId)
                                set(it.email, request.email)
                                set(it.nickname, request.nickName)
                                set(it.type, SignType.KAKAO)
                            }
                        }.let {
                            it as Int
                        }

                        database.users.find { it.id eq id }!!
                    }

                    val token = user.generateToken(application.environment.config)

                    call.response.cookies.apply {
                        append(
                            Cookie(
                                "access-token",
                                token.accessToken,
                                maxAge = 60 * 60 * 24 * 7,
                                httpOnly = true,
                                secure = application.environment.config.getAppMode() == AppMode.PROD
                            )
                        )
                        append(
                            Cookie(
                                "refresh-token",
                                token.refreshToken,
                                maxAge = 60 * 60 * 24 * 30,
                                httpOnly = true,
                                secure = application.environment.config.getAppMode() == AppMode.PROD
                            )
                        )
                    }

                    call.respond(HttpStatusCode.OK, Response.Success(user, "회원가입에 성공했습니다."))
                    return@post
                }

            }

        }


    }


}