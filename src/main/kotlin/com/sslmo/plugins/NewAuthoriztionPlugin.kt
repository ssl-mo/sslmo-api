package com.sslmo.plugins

import Response
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.sslmo.database.DatabaseFactory
import com.sslmo.database.User
import com.sslmo.database.users
import com.sslmo.models.TokenType
import com.sslmo.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.slf4j.LoggerFactory
import java.util.*


fun Route.authorizedRoute(tokenType: TokenType, build: Route.() -> Unit): Route {

    val route = createChild(AuthorizedRouteSelector()).also {
        install(AuthorizedRoutePlugin) {
            type = tokenType
            it.build()
        }
    }

    return route
}


class AuthorizedRouteSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent

    override fun toString(): String {
        return "AuthorizedRoute"
    }
}

private val onCallUserKey = AttributeKey<User>("onCallUserKey")

val AuthorizedRoutePlugin =
    createRouteScopedPlugin(name = "NewAuthorizedRoutePlugin", createConfiguration = ::PluginConfiguration) {

        val config = environment?.config

        val logger = LoggerFactory.getLogger("AuthorizedRoutePlugin")

        pluginConfig.apply {
            onCall { call ->


                val database = DatabaseFactory.connect(config)

                call.request.headers["Authorization"]?.let { value ->

                    // get the Token in the Bearer Token
                    val token = value.split(" ")[1]

                    logger.info("token: $token")


                    val jwtAudience = config.getAppName()
                    val jwtIssuer = config.getAppHost()

                    when (type) {

                        TokenType.ACCESS -> {
                            val accessSecret = config.getAccessJWTSecret()


                            try {
                                val credential = JWT
                                    .require(Algorithm.HMAC256(accessSecret))
                                    .withAudience(jwtAudience)
                                    .withIssuer(jwtIssuer)
                                    .build()
                                    .verify(token)

                                // 토큰 만료 여부 확인
                                if (credential.expiresAt.before(Date())) {
                                    call.respond(
                                        HttpStatusCode.Unauthorized,
                                        Response.Error("만료된 토큰", "다시 로그인 해주세요")
                                    )
                                } else {

                                    credential.subject?.let { uuid ->
                                        database.users.find {
                                            it.uuid eq UUID.fromString(uuid)
                                            it.active eq true
                                        }?.let { user ->

                                            // attributes에 user를 저장
                                            call.attributes.put(onCallUserKey, user)
                                        } ?: run {
                                            call.respond(
                                                HttpStatusCode.Unauthorized,
                                                Response.Error("유효하지 않은 토큰", "다시 시도 해주세요")
                                            )
                                        }

                                    } ?: run {
                                        call.respond(
                                            HttpStatusCode.Unauthorized,
                                            Response.Error("유효하지 않은 토큰", "다시 시도 해주세요")
                                        )
                                    }
                                }


                            } catch (e: JWTVerificationException) {
                                call.respond(HttpStatusCode.Unauthorized, Response.Error(e.message, "다시 시도 해주세요"))
                            }


                        }

                        TokenType.REFRESH -> {
                            val refreshSecret = config.getRefreshJWTSecret()

                            try {
                                val credential = JWT
                                    .require(Algorithm.HMAC256(refreshSecret))
                                    .build()
                                    .verify(token)


                                if (credential.expiresAt.before(Date())) {
                                    call.respond(
                                        HttpStatusCode.Unauthorized,
                                        Response.Error("만료된 토큰", "다시 로그인 해주세요")
                                    )
                                } else {

                                    credential.getClaim("uuid").asString()?.let { uuid ->
                                        database.users.find {
                                            it.uuid eq UUID.fromString(uuid)
                                            it.active eq true
                                        }?.let { user ->


                                            val accessToken = user.getAccessToken(config!!)
                                            val refreshToken = user.getRefreshToken(config)

                                            call.respond(
                                                HttpStatusCode.OK,
                                                Response.Success(
                                                    data = mapOf<String, String>(
                                                        "access_token" to accessToken,
                                                        "refresh_token" to refreshToken
                                                    ), message = "토큰 재발급 성공"
                                                )
                                            )


                                        } ?: run {
                                            call.respond(
                                                HttpStatusCode.Unauthorized,
                                                Response.Error("유효하지 않은 토큰", "다시 시도 해주세요")
                                            )
                                        }

                                    } ?: run {
                                        call.respond(
                                            HttpStatusCode.Unauthorized,
                                            Response.Error("유효하지 않은 토큰", "다시 시도 해주세요")
                                        )
                                    }
                                }

                            } catch (_: JWTVerificationException) {
                                call.respond(HttpStatusCode.Unauthorized, Response.Error("유효하지 않은 토큰", "다시 시도 해주세요"))
                            }
                        }
                    }

                }


            }


        }
    }

class PluginConfiguration {
    lateinit var type: TokenType
}

fun ApplicationCall.getUser(): User {
    return attributes[onCallUserKey]
}