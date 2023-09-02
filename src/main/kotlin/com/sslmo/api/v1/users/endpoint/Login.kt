package com.sslmo.api.v1.users.endpoint

import Response
import com.sslmo.api.v1.users.models.EmailLoginRequest
import com.sslmo.api.v1.users.models.SocialLoginRequest
import com.sslmo.api.v1.users.models.User
import com.sslmo.api.v1.users.service.UserService
import com.sslmo.models.SignType
import com.sslmo.utils.setCookie
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.login() {

	val userService by inject<UserService>()

	route("/login") {
		// 이메일 로그인
		post("/email", {
			tags = listOf("User")
			summary = "이메일 로그인"
			request {
				body<EmailLoginRequest>()
			}
			response {
				HttpStatusCode.OK to {
					description = "success"
					body<Response.Success<User>>()
				}
				HttpStatusCode.BadRequest to {
					description = "bad_request"
					body<Response.Error<*>>()
				}
			}
		}) {
			val request = call.receive<EmailLoginRequest>()
			val user = userService.emailLogin(request.email, request.password)

			if (user == null) {
				call.respond(HttpStatusCode.NotFound, Response.Error("존재하지 않는 유저입니다.", "로그인에 실패하였습니다."))
			} else {
				val token = user.generateToken(application.environment.config)
				this.setCookie(token.accessToken, token.refreshToken)
				call.respond(HttpStatusCode.OK, Response.Success(user, "로그인에 성공하였습니다."))
			}
		}

		// 카카오 로그인
		post("/kakao", {
			tags = listOf("User")
			summary = "카카오 로그인"
			request {
				body<SocialLoginRequest>()
			}
			response {
				HttpStatusCode.OK to {
					description = "success"
					body<Response.Success<User>>()
				}
				HttpStatusCode.BadRequest to {
					description = "bad_request"
					body<Response.Error<*>>()
				}
			}
		}) {
			val request = call.receive<SocialLoginRequest>()
			val user = userService.socialLogin(request.socialId, SignType.KAKAO)
			val token = user.generateToken(application.environment.config)
			this.setCookie(token.accessToken, token.refreshToken)
			call.respond(HttpStatusCode.OK, Response.Success(user, "로그인에 성공하였습니다."))

		}

		// 네이버 로그인
		post("/naver", {
			tags = listOf("User")
			summary = "네이버 로그인"
			request {
				body<SocialLoginRequest>()
			}
			response {
				HttpStatusCode.OK to {
					description = "success"
					body<Response.Success<User>>()
				}
				HttpStatusCode.BadRequest to {
					description = "bad_request"
					body<Response.Error<*>>()
				}
			}
		}) {
			val request = call.receive<SocialLoginRequest>()
			val user = userService.socialLogin(request.socialId, SignType.NAVER)
			val token = user.generateToken(application.environment.config)
			this.setCookie(token.accessToken, token.refreshToken)
			call.respond(HttpStatusCode.OK, Response.Success(user, "로그인에 성공하였습니다."))
		}

		// 구글 로그인
		post("/google", {
			tags = listOf("User")
			summary = "Google 로그인"
			request {
				body<SocialLoginRequest>()
			}
			response {
				HttpStatusCode.OK to {
					description = "success"
					body<Response.Success<User>>()
				}
				HttpStatusCode.BadRequest to {
					description = "bad_request"
					body<Response.Error<*>>()
				}
			}
		}) {
			val request = call.receive<SocialLoginRequest>()
			val user = userService.socialLogin(request.socialId, SignType.GOOGLE)
			val token = user.generateToken(application.environment.config)
			this.setCookie(token.accessToken, token.refreshToken)
			call.respond(HttpStatusCode.OK, Response.Success(user, "로그인에 성공하였습니다."))

		}

		// apple login
		post("/apple", {
			tags = listOf("User")
			summary = "Apple로 로그인"
			request {
				body<SocialLoginRequest>()
			}
			response {
				HttpStatusCode.OK to {
					description = "success"
					body<Response.Success<User>>()
				}
				HttpStatusCode.BadRequest to {
					description = "bad_request"
					body<Response.Error<*>>()
				}
			}
		}) {
			val request = call.receive<SocialLoginRequest>()
			val user = userService.socialLogin(request.socialId, SignType.KAKAO)
			val token = user.generateToken(application.environment.config)
			this.setCookie(token.accessToken, token.refreshToken)
			call.respond(HttpStatusCode.OK, Response.Success(user, "로그인에 성공하였습니다."))
		}
	}

}