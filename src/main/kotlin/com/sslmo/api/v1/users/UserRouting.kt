package com.sslmo.api.v1.users

import com.sslmo.api.v1.users.endpoint.login
import com.sslmo.api.v1.users.endpoint.register
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Route.usersRouting() {

	val logger = LoggerFactory.getLogger("Users Routing")

	route("/user") {
		login()
		register()
	}

}