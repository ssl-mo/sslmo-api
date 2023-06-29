package com.sslmo.v1.auth

import DefaultResponse
import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.database.DatabaseFactory
import com.sslmo.models.SignType
import com.sslmo.database.users
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.v3.oas.annotations.media.Schema
import kotlinx.serialization.Serializable
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import java.security.SecureRandom

fun Routing.signIn(
    group: String,
) {
    post("$group/sign", {
        tags = listOf("Auth")
        summary = "로그인"
        request {
            body<SignInRequest>()
        }
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<DefaultResponse>()
            }
            HttpStatusCode.BadRequest to {
                description = "bad_request"
                body<DefaultResponse>()
            }
        }
    }) { _ ->
        val request = call.receive<SignInRequest>()

        val database = DatabaseFactory.connect(application.environment.config)

        when (request.type) {
            SignType.EMAIL -> {
                val user = database.users.find {
                    it.type eq request.type
                    it.email eq request.email
                }

                user?.let {
                    val verified = BCrypt.verifyer().verify(request.password!!.toCharArray(), it.password!!).verified
                    if (!verified) {
                        call.respond(HttpStatusCode.NotFound, DefaultResponse(message = "bad_password"))
                    }

                } ?: run {
                    call.respond(HttpStatusCode.NotFound, DefaultResponse(message = "not_found_user"))
                }
            }

            else -> {
                val user = database.users.find {
                    it.type eq request.type
                    it.email eq request.email
                    it.socialId eq request.socialId!!
                }

                user?.let {

                } ?: run {
                    call.respond(HttpStatusCode.NotFound, DefaultResponse(message = "not_found_user"))
                }
            }
        }

        call.respond(DefaultResponse(message = "success"))
    }
}

@Serializable
data class SignInRequest(
    @field:Schema(required = true) val type: SignType,
    @field:Schema(required = true) val email: String,

    val socialId: String? = null,
    val password: String? = null,
    val token: String? = null,
)