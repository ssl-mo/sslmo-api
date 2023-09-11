package com.sslmo.api.v1.users.endpoint

import Response
import com.sslmo.api.v1.users.models.*
import com.sslmo.api.v1.users.service.UserService
import com.sslmo.models.TokenType
import com.sslmo.plugins.getUser
import com.sslmo.plugins.withCookie
import com.sslmo.system.error.ErrorMessage
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.patch
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.register() {

	val userService by inject<UserService>()

	get("/check-email", {
		tags = listOf("User")
		summary = "이메일 확인"
		request {
			body<EmailCheckRequest>()
		}
		response {
			HttpStatusCode.OK to {
				description = "성공"
				body<Response.Success<*>>()
			}

			HttpStatusCode.Conflict to {
				description = "conflict"
				body<Response.Error>()
			}
		}
	}) {
		val body = call.receive<EmailCheckRequest>()
		val user = userService.checkEmailExist(body.email)

		if (user) {
			call.respond(
				HttpStatusCode.Conflict,
				Response.Error(
					message = "duplicate",
					description = ErrorMessage.USER_EXIST
				)
			)
		} else {
			call.respond(HttpStatusCode.OK, Response.Success(data = "가입 가능한 이메일입니다.", message = "성공"))
		}
	}

	route("/register") {

		post("/email", {
			tags = listOf("User")
			summary = "회원 가입"
			request {
				body<EmailLoginRequest>()
			}
			response {
				HttpStatusCode.OK to {
					description = "success"
					body<Response.Success<*>> {
					}
				}
				HttpStatusCode.Conflict to {
					description = "duplicate"
					body<Response.Error>()
				}
			}
		})
		{

			val request = call.receive<EmailRegisterRequest>()
			val user = userService.register(request)
			call.respond(
				HttpStatusCode.OK,
				Response.Success(LoginResponse(user, user.generateToken(application.environment.config)), "회원가입에 성공")
			)
//
		}

		// 소셜 로그인(가입)
		post("/social", {
			tags = listOf("User")
			summary = "소셜 가입"
			request {
				body<SocialRegisterRequest>()
			}
			response {
				HttpStatusCode.OK to {
					description = "success"
					body<Response.Success<*>>()
				}
				HttpStatusCode.Conflict to {
					description = "duplicate"
					body<Response.Error>()
				}
			}
		}) {
			val request = call.receive<SocialRegisterRequest>()
			val user = userService.register(request)
			call.respond(
				HttpStatusCode.OK,
				Response.Success(LoginResponse(user, user.generateToken(application.environment.config)), "회원가입에 성공")
			)
		}
	}

	withCookie(TokenType.ACCESS) {

		patch("/update-address", {
			tags = listOf("User")
			protected = true
			summary = "주소 업데이트"
			request {
				body<UpdateAddressRequest>()
			}

			response {
				HttpStatusCode.OK to {
					description = "업데이트 성공"
					body<Response.Success<Boolean>>()
				}

				HttpStatusCode.ExpectationFailed to {
					description = "invalid_password"
					body<Response.Error>()
				}
			}
		}) {

			val body = call.receive<UpdateAddressRequest>()
			val user = call.getUser()
			val result = userService.updateAddress(user.uuid, body)

			if (result) {
				call.respond(HttpStatusCode.OK, Response.Success(true, "업데이트 성공"))
			} else {
				call.respond(
					HttpStatusCode.ExpectationFailed,
					Response.Error("invalid_password", ErrorMessage.INVALID_PASSWORD)
				)
			}
		}
	}
}