package com.sslmo.database.tables

import com.sslmo.database.sql_types.uuid
import com.sslmo.models.SignType
import com.sslmo.models.user.User
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.LocalDate
import java.util.*


object Users : BaseTable<User>("user") {
	val id = int("id").primaryKey()
	val uuid = uuid("uuid")
	val type = enum<SignType>("type")
	val email = varchar("email")
	val socialId = varchar("social_id")
	val password = varchar("password")
	val nickname = varchar("nickname")
	val notification = boolean("notification")
	val active = boolean("active")
	val inActiveAt = date("in_active_at")
	val createdAt = date("created_at")
	val updatedAt = date("updated_at")

	override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) = User(
		id = row[id] ?: 0,
		uuid = row[uuid] ?: UUID.randomUUID(),
		type = row[type] ?: SignType.EMAIL,
		socialId = row[socialId],
		email = row[email] ?: "",
		password = row[password],
		nickname = row[nickname] ?: "",
		notification = row[notification] ?: false,
		active = row[active] ?: false,
		inActiveAt = row[inActiveAt],
		createdAt = row[createdAt] ?: LocalDate.now(),
		updatedAt = row[updatedAt],
	)
}

val Database.users get() = this.sequenceOf(Users)