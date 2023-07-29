//package com.sslmo.api.v1.auth
//
//import DefaultResponse
//import at.favre.lib.crypto.bcrypt.BCrypt
//import com.sslmo.database.DatabaseFactory
//import com.sslmo.database.users
//import com.sslmo.models.AuthorizedType
//import com.sslmo.models.SignType
//import com.sslmo.models.user.LoginRequest
//import com.sslmo.plugins.authorizedRoute
//import com.sslmo.utils.getAccessToken
//import com.sslmo.utils.getRefreshToken
//import com.sslmo.utils.setCookie
//import io.github.smiley4.ktorswaggerui.dsl.post
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.request.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import org.ktorm.dsl.and
//import org.ktorm.dsl.eq
//import org.ktorm.entity.find
//
//fun Route.signIn() {
//
//    authorizedRoute(listOf(AuthorizedType.APP)) {
//
//        post("/sign", {
//            tags = listOf("Auth")
//            protected = true
//            summary = "로그인"
//            request {
//                body<LoginRequest>()
//            }
//            response {
//                HttpStatusCode.OK to {
//                    description = "success"
//                    body<DefaultResponse>()
//                }
//                HttpStatusCode.BadRequest to {
//                    description = "bad_request"
//                    body<DefaultResponse>()
//                }
//            }
//        }) { _ ->
//            val request = call.receive<LoginRequest>()
//
//            val config = application.environment.config
//
//            val database = DatabaseFactory.connect(config)
//
//            when (request.type) {
//                SignType.EMAIL -> {
//                    val user = database.users.find {
//                        (it.type eq request.type) and
//                                (it.email eq request.email)
//                    }
//
//                    user?.let {
//                        val verified =
//                            BCrypt.verifyer().verify(request.password!!.toCharArray(), it.password!!).verified
//                        if (!verified) {
//                            call.respond(HttpStatusCode.NotFound, DefaultResponse(message = "bad_password"))
//                        }
//
//                        setCookie("access-token", it.getAccessToken(config), 60 * 30)
//                        setCookie("refresh-token", it.getRefreshToken(config), 60 * 60 * 24 * 14)
//
//                    } ?: run {
//                        call.respond(HttpStatusCode.NotFound, DefaultResponse(message = "not_found_user"))
//                    }
//                }
//
//                else -> {
//                    val user = database.users.find {
//                        (it.type eq request.type) and
//                                (it.email eq request.email) and
//                                (it.socialId eq request.socialId!!)
//                    }
//
//                    user?.let {
//                        setCookie("access-token", it.getAccessToken(config), 60 * 30)
//                        setCookie("refresh-token", it.getRefreshToken(config), 60 * 60 * 24 * 14)
//                    } ?: run {
//                        call.respond(HttpStatusCode.NotFound, DefaultResponse(message = "not_found_user"))
//                    }
//                }
//            }
//
//            call.respond(DefaultResponse(message = "success"))
//        }
//    }
//}
//
//
//
