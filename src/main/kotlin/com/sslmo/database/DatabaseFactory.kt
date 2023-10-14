package com.sslmo.database

import com.sslmo.utils.getDBPassword
import com.sslmo.utils.getDBUrl
import com.sslmo.utils.getDBUser
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

	private lateinit var database: Database


	fun init(config: ApplicationConfig?) {
		val url = config.getDBUrl()
		val user = config.getDBUser()
		val password = config.getDBPassword()

		database = Database.connect(createHikariDataSource(url, user, password))
	}

	fun <T> dbQuery(block: (database: Database) -> T): T {
		return transaction {
			block(database)
		}
	}

	private fun createHikariDataSource(
		url: String,
		user: String,
		password: String,
	) = HikariDataSource(HikariConfig().apply {
		jdbcUrl = url
		driverClassName = "com.mysql.cj.jdbc.Driver"
		username = user
		setPassword(password)
		maximumPoolSize = 3
		validate()
	})

}