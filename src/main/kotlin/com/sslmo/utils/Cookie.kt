package com.sslmo.utils

import com.sslmo.models.AppMode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.setCookie(
    accessToken: String,
    refreshToken: String,
) {
    val config = application.environment.config

    val secure = config.getAppMode() != AppMode.LOCAL
    val httpOnly = config.getAppMode() != AppMode.LOCAL

    call.response.cookies.apply {
        append(
            Cookie(
                "access-token",
                accessToken,
                maxAge = 60 * 60 * 24 * 7,
                httpOnly = httpOnly,
                secure = secure
            )
        )
        append(
            Cookie(
                "refresh-token",
                refreshToken,
                maxAge = 60 * 60 * 24 * 30,
                httpOnly = httpOnly,
                secure = secure
            )
        )
    }
}