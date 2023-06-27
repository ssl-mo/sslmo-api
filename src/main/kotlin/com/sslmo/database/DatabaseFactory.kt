package com.sslmo.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.ktorm.database.Database

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        val url = config.propertyOrNull("database.url")?.getString() ?: "jdbc:mysql://localhost:3306/sslmo"
        val user = config.propertyOrNull("database.user")?.getString() ?: "root"
        val password = config.propertyOrNull("database.password")?.getString() ?: "1234"

        val database = Database.connect(createHikariDataSource(url, user, password))
    }

    fun connect(config: ApplicationConfig): Database {
        val url = config.propertyOrNull("database.url")?.getString() ?: "jdbc:mysql://localhost:3306/sslmo"
        val user = config.propertyOrNull("database.user")?.getString() ?: "root"
        val password = config.propertyOrNull("database.password")?.getString() ?: "1234"

        return Database.connect(createHikariDataSource(url, user, password))
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