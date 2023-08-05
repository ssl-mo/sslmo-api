package com.sslmo.api.v1.users

import Response
import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.database.DatabaseFactory
import com.sslmo.database.DatabaseFactory.dbQuery
import com.sslmo.database.tables.Users
import com.sslmo.database.tables.users
import com.sslmo.models.SignType
import com.sslmo.models.user.LoginRequest
import com.sslmo.models.user.UserRegisterRequest
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.find

fun Route.register() {
    post("/register", {
        tags = listOf("User")
        protected = true
        summary = "회원 가입"
        request {
            body<UserRegisterRequest>()
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

        val request = call.receive<UserRegisterRequest>()


        withContext(Dispatchers.IO) {

            val database = DatabaseFactory.connect()


            // 닉네임 중복 확인
            database.users.find {
                it.nickname eq request.nickname
            }?.let {
                call.respond(HttpStatusCode.Conflict, Response.Error("이미 존재하는 닉네임입니다. 다른 닉네임을 사용해주세요", "가입 실패"))
                return@withContext
            } ?: run {

                val hashPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

                when (request.type) {

                    // 이메일 가입
                    SignType.EMAIL -> {

                        // 이메일 중복 확인
                        database.users.find {
                            (it.email eq request.email) and (it.type eq request.type)
                        }?.also {

                            // 이미 존재하는 이메일
                            call.respond(
                                HttpStatusCode.Conflict,
                                Response.Error("이미 존재하는 이메일입니다. 다른 이메일을 사용해주세요", "가입 실패")
                            )
                        } ?: run {
                            // 가입
                            dbQuery { database ->
                                database.insertAndGenerateKey(Users) {
                                    set(it.socialId, request.socialId)
                                    set(it.email, request.email)
                                    set(it.nickname, request.nickname)
                                    set(it.password, hashPassword)
                                    set(it.type, request.type)
                                }
                            }

                            call.respond(HttpStatusCode.OK, Response.Success("성공", "가입에 성공했습니다."))
                        }
                    }

                    //
                    else -> {}
                }
            }

        }
    }
}