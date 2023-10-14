package com.sslmo.database.tables

import com.sslmo.api.v1.users.models.UserModel
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

	fun toUserModel(): UserModel {
		return UserModel(
			id.value,
			uuid,
			type,
			socialId,
			email,
			password,
			nickname,
			notification,
			active,
			inActiveAt,
			createdAt,
			updatedAt,
			siCode,
			guCode,
			dongCode

		)
	}
}

object Users : IntIdTable("user", "id") {
	val uuid = uuid("uuid").autoGenerate().uniqueIndex()
	val type = enumerationByName<SignType>("type", 10).uniqueIndex()
	val email = varchar("email", 191).uniqueIndex()
	val socialId = varchar("social_id", 191).nullable()
	val password = varchar("password", 191)
	val nickname = varchar("nickname", 191)
	val notification = bool("notification")
	val active = bool("active")
	val inActiveAt = datetime("in_active_at").nullable()
	val createdAt = datetime("created_at").nullable().clientDefault { LocalDateTime.now() }
	val updatedAt = datetime("updated_at").nullable().clientDefault { LocalDateTime.now() }
	val siCode = long("si_code").nullable()
	val guCode = long("gu_code").nullable()
	val dongCode = long("dong_code").nullable()
}

