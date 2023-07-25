package com.sslmo

import com.sslmo.database.DatabaseFactory
import com.sslmo.modules.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment.config)

    // XForwardedHeaders
    configureXFormHeaders()

    // Json
    configureJson()

    // RequestValidation
    configureRequestValidation()

    // StatusPage
    configureStatusPage()

    // Authorization
    configureAuthorization()

    // Routing
    configureRouting()
}