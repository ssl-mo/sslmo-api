package com.sslmo.api.v1.users.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.api.v1.users.models.*
import com.sslmo.database.DatabaseFactory.dbQuery
import com.sslmo.database.tables.Users
import com.sslmo.database.tables.users
import com.sslmo.models.SignType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Module
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.insertAndGenerateKey
import org.ktorm.dsl.update
import org.ktorm.entity.filter
import org.ktorm.entity.find
import java.util.*


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
					(it.email eq email) and
							(it.type eq SignType.EMAIL)
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
		when (registerRequest) {
			is EmailRegisterRequest -> {
				val hashedPassword = BCrypt.withDefaults().hashToString(12, registerRequest.password.toCharArray())
				return withContext(Dispatchers.IO) {
					dbQuery { database ->
						database.insertAndGenerateKey(Users) {
							set(it.email, registerRequest.email)
							set(it.password, hashedPassword)
							set(it.nickname, registerRequest.nickName)
							set(it.type, SignType.EMAIL)
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
						}.let { it as Int }
					}
				}
			}

		}
	}

	suspend fun resetPassword(userId: UUID, hashedPassword: String): Boolean {

		return runCatching {
			withContext(Dispatchers.IO) {
				dbQuery { database ->
					database.update(Users) {
						set(it.password, hashedPassword)
						where { it.uuid eq userId }
					}
				}
			}
		}.isSuccess
	}

	suspend fun updateAddress(userId: UUID, address: UpdateAddressRequest): Boolean {

		return runCatching {
			withContext(Dispatchers.IO) {
				dbQuery { database ->
					database.update(Users) {
						set(it.siCode, address.siCode)
						set(it.guCode, address.guCode)
						set(it.dongCode, address.dongCode)
						where { it.uuid eq userId }
					}
				}

			}
		}.isSuccess
	}
}