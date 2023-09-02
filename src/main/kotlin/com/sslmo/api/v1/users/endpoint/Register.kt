package com.sslmo.api.v1.users.endpoint

import Response
import com.sslmo.api.v1.users.models.EmailLoginRequest
import com.sslmo.api.v1.users.models.EmailRegisterRequest
import com.sslmo.api.v1.users.models.LoginResponse
import com.sslmo.api.v1.users.models.SocialRegisterRequest
import com.sslmo.api.v1.users.service.UserService
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.register() {

	val userService by inject<UserService>()

	route("/register") {

		// 이메일로 가입
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
				HttpStatusCode.BadRequest to {
					description = "bad_request"
					body<Response.Error<*>>()
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
			val user = userService.register(request)
			call.respond(
				HttpStatusCode.OK,
				Response.Success(LoginResponse(user, user.generateToken(application.environment.config)), "회원가입에 성공")
			)
		}
	}
}