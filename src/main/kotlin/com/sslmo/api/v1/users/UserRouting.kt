package com.sslmo.api.v1.users

import Response
import com.sslmo.api.v1.users.endpoint.login
import com.sslmo.api.v1.users.endpoint.register
import com.sslmo.api.v1.users.models.EmailCheckRequest
import com.sslmo.api.v1.users.service.UserService
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import org.slf4j.LoggerFactory

fun Route.usersRouting() {

	val logger = LoggerFactory.getLogger("Users Routing")

	route("/user") {
		login()
		register()

		get("/check-email", {
			tags = listOf("User")
			summary = "이메일 확인"
			request {
				body<EmailCheckRequest>()
			}
			response {
				HttpStatusCode.OK to {
					description = "성공"
					body { Response.Success(data = "가입 가능한 이메일입니다.", message = "성공") }
				}

				HttpStatusCode.Conflict to {
					description = "실패"
					body { Response.Error(error = "이미 가입한 이메일입니다. 로그인을 시도해주세요.", message = "실패") }
				}
			}
		}) {

			val userService by inject<UserService>(clazz = UserService::class.java)
			val body = call.receive<EmailCheckRequest>()
			val user = userService.checkEmailExist(body.email)

			if (user) {
				call.respond(
					HttpStatusCode.Conflict,
					Response.Error(error = "이미 가입한 이메일입니다. 로그인을 시도해주세요.", message = "실패")
				)
			} else {
				call.respond(HttpStatusCode.OK, Response.Success(data = "가입 가능한 이메일입니다.", message = "성공"))
			}
		}
	}

}