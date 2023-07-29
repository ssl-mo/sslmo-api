//package com.sslmo.api.v1.users
//
//import DefaultResponse
//import at.favre.lib.crypto.bcrypt.BCrypt
//import com.sslmo.database.DatabaseFactory
//import com.sslmo.database.Users
//import com.sslmo.database.users
//import com.sslmo.models.AuthorizedType
//import com.sslmo.models.SignType
//import com.sslmo.plugins.authorizedRoute
//import io.github.smiley4.ktorswaggerui.dsl.post
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.request.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import io.swagger.v3.oas.annotations.media.Schema
//import kotlinx.serialization.Serializable
//import org.ktorm.dsl.and
//import org.ktorm.dsl.eq
//import org.ktorm.dsl.insert
//import org.ktorm.entity.find
//
//fun Route.signUp(
//
//) {
//    authorizedRoute(listOf(AuthorizedType.APP)) {
//        post({
//            tags = listOf("Users")
//            protected = true
//            summary = "회원가입"
//            request {
//                body<SignUpRequest>()
//            }
//            response {
//                HttpStatusCode.OK to {
//                    description = "success"
//                    body<DefaultResponse>()
//                }
//                HttpStatusCode.Conflict to {
//                    description = "duplicated_nickname, duplicated_user"
//                    body<DefaultResponse>()
//                }
//            }
//        }) { _ ->
//            val request = call.receive<SignUpRequest>()
//
//            val config = application.environment.config
//
//            val database = DatabaseFactory.connect(config)
//
//            database.users.find {
//                it.nickname eq request.nickname
//            }?.let {
//                call.respond(HttpStatusCode.Conflict, DefaultResponse(message = "duplicated_nickname"))
//            } ?: run {
//                val hashPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())
//
//                when (request.type) {
//                    SignType.EMAIL -> {
//                        val user = database.users.find {
//                            (it.type eq request.type) and
//                                    (it.email eq request.email)
//                        }
//
//                        user?.let {
//                            call.respond(HttpStatusCode.Conflict, DefaultResponse(message = "duplicated_user"))
//                        } ?: run {
//                            database.insert(Users) {
//                                set(it.type, request.type)
//                                set(it.email, request.email)
//                                set(it.nickname, request.nickname)
//                                set(it.password, hashPassword)
//                            }
//                        }
//                    }
//
//                    else -> {
//                        val user = database.users.find {
//                            (it.type eq request.type) and
//                                    (it.email eq request.email) and
//                                    (it.socialId eq request.socialId!!)
//                        }
//
//                        user?.let {
//                            call.respond(HttpStatusCode.Conflict, DefaultResponse(message = "duplicated_user"))
//                        } ?: run {
//                            database.insert(Users) {
//                                set(it.type, request.type)
//                                set(it.email, request.email)
//                                set(it.nickname, request.nickname)
//                                set(it.password, hashPassword)
//                                set(it.socialId, request.socialId)
//                            }
//                        }
//                    }
//                }
//            }
//
//            call.respond(DefaultResponse(message = "success"))
//        }
//    }
//}
//
//@Serializable
//data class SignUpRequest(
//    @field:Schema(required = true) val type: SignType,
//    @field:Schema(required = true) val email: String,
//    @field:Schema(required = true) val nickname: String,
//    @field:Schema(required = true) val password: String,
//
//    val socialId: String? = null,
//)