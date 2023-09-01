package com.sslmo.database.tables

import com.sslmo.api.v1.users.models.User
import com.sslmo.database.sql_types.uuid
import com.sslmo.models.SignType
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
	val siCode = long("si_code")
	val guCode = long("gu_code")
	val dongCode = long("dong_code")

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
		siCode = row[siCode] ?: 0,
		guCode = row[guCode] ?: 0,
		dongCode = row[dongCode] ?: 0

	)
}

val Database.users get() = this.sequenceOf(Users)