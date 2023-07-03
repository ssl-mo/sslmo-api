package com.sslmo.utils

import com.sslmo.models.AppMode
import io.ktor.server.application.*
import io.ktor.util.pipeline.*

fun PipelineContext<*, ApplicationCall>.setCookie(
    name: String,
    value: String,
    maxAge: Long,
) {

    val config = application.environment.config

    val secure = config.getAppMode() != AppMode.LOCAL
    val httpOnly = config.getAppMode() != AppMode.LOCAL

    call.response.cookies.append(
        name,
        value,
        maxAge = maxAge,
        path = "/",
        secure = secure,
        httpOnly = httpOnly
    )
}