package com.sslmo.api.v1.users.endpoint

import Response
import com.sslmo.api.v1.users.models.PasswordChangeRequest
import com.sslmo.api.v1.users.service.UserService
import com.sslmo.models.TokenType
import com.sslmo.plugins.getUser
import com.sslmo.plugins.withCookie
import io.github.smiley4.ktorswaggerui.dsl.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory

fun Route.resetPassword() {

	val logger = LoggerFactory.getLogger("Password Routing")
	val userService by inject<UserService>()


	withCookie(TokenType.ACCESS) {
		put("/reset-password", {
			tags = listOf("User")
			summary = "비밀 번호 변경"
			headers {
				append(HttpHeaders.ContentType, ContentType.Application.Json)
			}
			request {
				body<PasswordChangeRequest>()
			}
			response {
				HttpStatusCode.OK to {
					description = "비밀번호 변경 성공"
					body<Response.Success<*>>()
				}
				HttpStatusCode.BadRequest to {
					description = "비밀번호 변경 실패"
					body<Response.Error<*>>()
				}
			}
		}) {
			val body = call.receive<PasswordChangeRequest>()
			val user = call.getUser()

			val result = userService.resetPassword(user.uuid, body.newPassword)

			if (result) {
				call.respond(HttpStatusCode.OK, Response.Success("비밀 번호 변경에 성공했습니다.", "재 로그인을 진행해주세요"))
			} else {
				call.respond(HttpStatusCode.BadRequest, Response.Error("비밀 번호 변경에 실패했습니다.", "실패"))
			}
		}
	}
}