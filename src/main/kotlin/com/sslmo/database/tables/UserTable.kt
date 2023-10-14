package com.sslmo.database.tables

import com.sslmo.models.SignType
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

import java.time.LocalDateTime

class User(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<User>(Users)

	var uuid by Users.uuid
	var type by Users.type
	var email by Users.email
	var socialId by Users.socialId
	var password by Users.password
	var nickname by Users.nickname
	var notification by Users.notification
	var active by Users.active
	var inActiveAt by Users.inActiveAt
	var createdAt by Users.createdAt
	var updatedAt by Users.updatedAt
	var siCode by Users.siCode
	var guCode by Users.guCode
	var dongCode by Users.dongCode
}

object Users : IntIdTable("user", "id") {
	val uuid = uuid("uuid").autoGenerate().uniqueIndex()
	val type = enumeration("type", SignType::class).uniqueIndex()
	val email = varchar("email", 191).uniqueIndex()
	val socialId = varchar("social_id", 191)
	val password = varchar("password", 191)
	val nickname = varchar("nickname", 191)
	val notification = bool("notification")
	val active = bool("active")
	val inActiveAt = datetime("in_active_at").clientDefault { LocalDateTime.now() }
	val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
	val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
	val siCode = long("si_code")
	val guCode = long("gu_code")
	val dongCode = long("dong_code")
//	val id = int("id").primaryKey()
//	val uuid = uuid("uuid")
//	val type = enum<SignType>("type")
//	val email = varchar("email")
//	val socialId = varchar("social_id")
//	val password = varchar("password")
//	val nickname = varchar("nickname")
//	val notification = boolean("notification")
//	val active = boolean("active")
//	val inActiveAt = date("in_active_at")
//	val createdAt = date("created_at")
//	val updatedAt = date("updated_at")
//	val siCode = long("si_code")
//	val guCode = long("gu_code")
//	val dongCode = long("dong_code")
//
//	override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean) = User(
//		id = row[id]!!,
//		uuid = row[uuid] ?: UUID.randomUUID(),
//		type = row[type] ?: SignType.EMAIL,
//		socialId = row[socialId],
//		email = row[email] ?: "",
//		password = row[password],
//		nickname = row[nickname] ?: "",
//		notification = row[notification] ?: false,
//		active = row[active] ?: false,
//		inActiveAt = row[inActiveAt],
//		createdAt = row[createdAt] ?: LocalDate.now(),
//		updatedAt = row[updatedAt],
//		siCode = row[siCode],
//		guCode = row[guCode],
//		dongCode = row[dongCode]
//	)
}

