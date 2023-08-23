package com.sslmo.api.v1.users.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.database.DatabaseFactory.dbQuery
import com.sslmo.database.tables.Users
import com.sslmo.database.tables.users
import com.sslmo.models.SignType
import com.sslmo.models.user.BaseRegisterRequest
import com.sslmo.models.user.EmailRegisterRequest
import com.sslmo.models.user.SocialRegisterRequest
import com.sslmo.models.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Module
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.entity.filter
import org.ktorm.entity.find


@Module
class UserRepository {

	suspend fun findById(id: Int): User {
		return withContext(Dispatchers.IO) {
			dbQuery { database ->
				database.users.find {
					it.id eq id
				}!!
			}
		}
	}

	suspend fun findBySocialId(socialId: String, signType: SignType): User? {
		return withContext(Dispatchers.IO) {
			dbQuery { database ->
				database.users.filter {
					it.type eq signType
				}.find {
					it.socialId eq socialId
				}
			}
		}
	}

	suspend fun findByEmail(email: String): User? {
		return withContext(Dispatchers.IO) {
			dbQuery { database ->
				database.users.find {
					it.email eq email
				}
			}
		}
	}


	suspend fun findByNickname(nickname: String): User? {
		return withContext(Dispatchers.IO) {
			dbQuery { database ->
				database.users.find {
					it.nickname eq nickname
				}
			}
		}
	}

	suspend fun addNewUser(registerRequest: BaseRegisterRequest): Int {

		return when (registerRequest) {
			is EmailRegisterRequest -> {
				val hashedPassword = BCrypt.withDefaults().hashToString(12, registerRequest.password.toCharArray())
				return withContext(Dispatchers.IO) {
					dbQuery { database ->
						database.insertAndGenerateKey(Users) {
							set(it.email, registerRequest.email)
							set(it.password, hashedPassword)
							set(it.nickname, registerRequest.nickName)
							set(it.type, SignType.EMAIL)
							set(it.siCode, registerRequest.siCode)
							set(it.guCode, registerRequest.guCode)
							set(it.dongCode, registerRequest.dongCode)
						}
					}.let { it as Int }
				}
			}

			is SocialRegisterRequest -> {
				return withContext(Dispatchers.IO) {
					dbQuery { database ->
						database.insertAndGenerateKey(Users) {
							set(it.email, registerRequest.email)
							set(it.socialId, registerRequest.socialId)
							set(it.nickname, registerRequest.nickName)
							set(it.type, registerRequest.type)
							set(it.siCode, registerRequest.siCode)
							set(it.guCode, registerRequest.guCode)
							set(it.dongCode, registerRequest.dongCode)
						}.let { it as Int }
					}
				}
			}

		}
	}
}