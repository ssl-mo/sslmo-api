package com.sslmo.database

import com.sslmo.utils.getDBPassword
import com.sslmo.utils.getDBUrl
import com.sslmo.utils.getDBUser
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.ktorm.database.Database

object DatabaseFactory {
    fun init(config: ApplicationConfig?) {
        val url = config.getDBUrl()
        val user = config.getDBUser()
        val password = config.getDBPassword()

        val database = Database.connect(createHikariDataSource(url, user, password))
    }

    fun connect(config: ApplicationConfig?): Database {
        val url = config.getDBUrl()
        val user = config.getDBUser()
        val password = config.getDBPassword()

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