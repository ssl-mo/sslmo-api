package com.sslmo.plugins

import DefaultResponse
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.sslmo.database.DatabaseFactory
import com.sslmo.database.User
import com.sslmo.database.users
import com.sslmo.models.AuthorizedType
import com.sslmo.utils.getAccessJWTSecret
import com.sslmo.utils.getAppKeyHeader
import com.sslmo.utils.getAppKeyValue
import com.sslmo.utils.getRefreshJWTSecret
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import java.util.*

fun Route.authorizedRoute(authorizedTypes: List<AuthorizedType>, build: Route.() -> Unit): Route {
    return createChild(AuthorizedRouteSelector()).apply {
        install(AuthorizedRoutePlugin) {
            types = authorizedTypes
        }
        build()
    }
}

class AuthorizedRouteSelector : RouteSelector() {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent
}

val onCallUserKey = AttributeKey<User>("onCallUserKey")

val AuthorizedRoutePlugin = createRouteScopedPlugin(
    name = "AuthorizedRoutePlugin",
    createConfiguration = ::PluginConfiguration
) {

    val config = environment?.config

    pluginConfig.apply {
        onCall { call ->
            types.forEach { type ->
                when (type) {
                    AuthorizedType.ACCESS -> {
                        val accessSecret = config.getAccessJWTSecret()

                        call.request.cookies["access-token"]?.let { accessToken ->
                            try {
                                val credential = JWT
                                    .require(Algorithm.HMAC256(accessSecret))
                                    .build()
                                    .verify(accessToken)

                                val database = DatabaseFactory.connect(config)

                                credential.claims["uuid"]?.toString()?.replace("\"", "")?.let { uuidString ->
                                    database.users.find {
                                        it.uuid eq UUID.fromString(uuidString)
                                        it.active eq true
                                    }?.let { user ->
                                        call.attributes.put(onCallUserKey, user)
                                    } ?: run {
                                        call.respond(
                                            HttpStatusCode.Unauthorized,
                                            DefaultResponse(message = "unauthorized_access_token")
                                        )
                                    }
                                } ?: run {
                                    call.respond(
                                        HttpStatusCode.Unauthorized,
                                        DefaultResponse(message = "unauthorized_access_token")
                                    )
                                }

                            } catch (_: JWTVerificationException) {
                                call.respond(
                                    HttpStatusCode.Unauthorized,
                                    DefaultResponse(message = "unauthorized_access_token")
                                )
                            }
                        } ?: run {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                DefaultResponse(message = "unauthorized_access_token")
                            )
                        }
                    }

                    AuthorizedType.REFRESH -> {
                        val refreshSecret = config.getRefreshJWTSecret()

                        call.request.cookies["refresh-token"]?.let { refreshToken ->
                            try {
                                val credential = JWT
                                    .require(Algorithm.HMAC256(refreshSecret))
                                    .build()
                                    .verify(refreshToken)

                                val database = DatabaseFactory.connect(config)

                                credential.claims["uuid"]?.toString()?.replace("\"", "")?.let { uuidString ->
                                    database.users.find {
                                        it.uuid eq UUID.fromString(uuidString)
                                        it.active eq true
                                    }?.let { user ->
                                        call.attributes.put(onCallUserKey, user)
                                    } ?: run {
                                        call.respond(
                                            HttpStatusCode.Unauthorized,
                                            DefaultResponse(message = "unauthorized_refresh_token")
                                        )
                                    }
                                } ?: run {
                                    call.respond(
                                        HttpStatusCode.Unauthorized,
                                        DefaultResponse(message = "unauthorized_refresh_token")
                                    )
                                }

                            } catch (_: JWTVerificationException) {
                                call.respond(
                                    HttpStatusCode.Unauthorized,
                                    DefaultResponse(message = "unauthorized_refresh_token")
                                )
                            }
                        } ?: run {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                DefaultResponse(message = "unauthorized_refresh_token")
                            )
                        }
                    }

                    AuthorizedType.APP -> {
                        val appKeyHeader = config.getAppKeyHeader()
                        val appKeyValue = config.getAppKeyValue()

                        call.request.headers[appKeyHeader]?.let {
                            if (it != appKeyValue) {
                                call.respond(
                                    HttpStatusCode.Unauthorized,
                                    DefaultResponse(message = "unauthorized_app_key")
                                )
                            }
                        } ?: run {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                DefaultResponse(message = "unauthorized_app_key")
                            )
                        }
                    }
                }
            }
        }
    }
}


class PluginConfiguration {
    lateinit var types: List<AuthorizedType>
}

fun ApplicationCall.getUser(): User {
    return attributes[onCallUserKey]
}