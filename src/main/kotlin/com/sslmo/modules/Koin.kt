package com.sslmo.modules

import com.sslmo.api.v1.users.repository.UserRepository
import com.sslmo.api.v1.users.service.UserService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
	val repository = module {
		single { UserRepository() }
	}

	val service = module {
		single { UserService(get()) }
	}
	install(Koin) {
		modules(repository, service)
	}
}


