package com.sslmo.modules

import DefaultResponse
import Response
import com.sslmo.system.error.DuplicateException
import com.sslmo.system.error.ErrorMessage
import com.sslmo.system.error.InValidPasswordException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.SerializationException

fun Application.configureStatusPage() {
	install(StatusPages) {
		exception<BadRequestException> { call, _ ->
			call.respond(
				HttpStatusCode.BadRequest,
				Response.Error(message = "bad_request", description = ErrorMessage.BAD_REQUEST)
			)
		}
//		exception<RequestValidationException> { call, e ->
//
//			call.respond(HttpStatusCode.BadRequest, Response.Error(e.reasons, "해당 필드에서 오류가 발생했습니다."))
//		}
		exception<SerializationException> { call, _ ->
			call.respond(HttpStatusCode.InternalServerError, DefaultResponse(message = "internal_server_error"))
		}
		exception<InValidPasswordException> { call, e ->
			call.respond(
				HttpStatusCode.Unauthorized,
				Response.Error("invalid_password", ErrorMessage.INVALID_PASSWORD)
			)
		}
		exception<DuplicateException> { call, e ->
			call.respond(HttpStatusCode.Conflict, Response.Error("duplicate", e.message))
		}
		exception<NotFoundException> { call, e ->
			call.respond(HttpStatusCode.NotFound, Response.Error("not_found", e.message!!))
		}

	}
}