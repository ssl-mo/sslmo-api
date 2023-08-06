package com.sslmo.plugins

import Response
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.sslmo.database.DatabaseFactory
import com.sslmo.database.tables.users
import com.sslmo.models.TokenType
import com.sslmo.models.user.User
import com.sslmo.utils.getAccessJWTSecret
import com.sslmo.utils.getAppHost
import com.sslmo.utils.getAppName
import com.sslmo.utils.getRefreshJWTSecret
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

                val database = DatabaseFactory.connect()
                val jwtAudience = config.getAppName()
                val jwtIssuer = config.getAppHost()


                when (type) {
                    TokenType.ACCESS -> {

                        val accessSecret = config.getAccessJWTSecret()
                        call.request.cookies["access-token"]?.let { accessToken ->

                            logger.info("token: $accessToken")


                            try {

                                val credential = JWT.require(Algorithm.HMAC256(accessSecret))
                                    .withAudience(jwtAudience)
                                    .withIssuer(jwtIssuer)
                                    .build()
                                    .verify(accessToken)


                                // 토큰 만료 여부 확인
                                if (credential.expiresAt.before(Date())) {
                                    call.respond(
                                        HttpStatusCode.Unauthorized,
                                        Response.Error("만료된 토큰", "다시 로그인 해주세요")
                                    )

                                } else if (TokenType.valueOf(credential.getClaim("type").asString()) != type) {

                                    call.respond(
                                        HttpStatusCode.Unauthorized,
                                        Response.Error("유효하지 않은 토큰", "다시 시도 해주세요")
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
                                call.respond(HttpStatusCode.Unauthorized, Response.Error("인증된 토큰이 아닙니다.", "다시 시도 해주세요"))
                            }
                        } ?: run {
                            call.respond(HttpStatusCode.Unauthorized, Response.Error("토큰이 없습니다.", "다시 시도 해주세요"))
                        }
                    }

                    TokenType.REFRESH -> {

                        val refreshSecret = config.getRefreshJWTSecret()

                        call.request.cookies["refresh-token"]?.let { refreshToken ->

                            try {
                                val credential = JWT.require(Algorithm.HMAC256(refreshSecret))
                                    .withAudience(jwtAudience)
                                    .withIssuer(jwtIssuer)
                                    .build()
                                    .verify(refreshToken)


                                if (credential.expiresAt.before(Date())) {
                                    call.respond(
                                        HttpStatusCode.Unauthorized,
                                        Response.Error("만료된 토큰", "다시 로그인 해주세요")
                                    )

                                } else if (TokenType.valueOf(
                                        credential.getClaim("type").asString()
                                    ) != TokenType.REFRESH
                                ) {
                                    call.respond(
                                        HttpStatusCode.Unauthorized,
                                        Response.Error("유효하지 않은 토큰", "다시 시도 해주세요")
                                    )
                                } else {

                                    credential.subject?.let { uuid ->
                                        database.users.find {
                                            it.uuid eq UUID.fromString(uuid)
                                            it.active eq true
                                        }?.let { user ->

                                            val token = user.generateToken(config!!)


                                            // 쿠키에 토큰 저장
                                            call.response.cookies.apply {
                                                append(
                                                    Cookie(
                                                        // access token
                                                        name = "access-token",
                                                        value = token.accessToken,
                                                        path = "/",
                                                        maxAge = 60 * 30,
                                                        secure = true,
                                                        httpOnly = true,
                                                    )
                                                )

                                                append(
                                                    Cookie(
                                                        // refresh token
                                                        name = "refresh-token",
                                                        value = token.refreshToken,
                                                        path = "/",
                                                        maxAge = 60 * 60 * 24 * 14,
                                                        secure = true,
                                                        httpOnly = true,
                                                    )
                                                )

                                            }


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
                                call.respond(HttpStatusCode.Unauthorized, Response.Error("인증된 토큰이 아닙니다.", "다시 시도 해주세요"))
                            }


                        } ?: run {
                            call.respond(HttpStatusCode.Unauthorized, Response.Error("토큰이 없습니다.", "다시 시도 해주세요"))
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