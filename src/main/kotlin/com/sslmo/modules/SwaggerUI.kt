package com.sslmo.modules

import com.sslmo.plugins.AuthorizedRouteSelector
import com.sslmo.utils.getAppHost
import com.sslmo.utils.getAppKeyHeader
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthKeyLocation
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.ktor.server.application.*


fun Application.configureSwaggerUI() {
    install(SwaggerUI) {
        ignoredRouteSelectors = listOf(AuthorizedRouteSelector::class)
        securityScheme(environment.config.getAppKeyHeader()) {
            type = AuthType.API_KEY
            location = AuthKeyLocation.HEADER
        }
        defaultSecuritySchemeName = environment.config.getAppKeyHeader()
        swagger {
            swaggerUrl = "swagger"
            forwardRoot = true
            authentication = "auth-basic"
        }
        info {
            title = "SSLMO API"
            version = "latest"
        }
        server {
            url = environment.config.getAppHost()
        }
    }
}