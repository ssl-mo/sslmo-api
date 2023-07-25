package com.sslmo.modules

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.forwardedheaders.*

fun Application.configureXFormHeaders() {
    install(XForwardedHeaders) {
        headers {
            headersOf()
        }
    }
}