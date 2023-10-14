package com.sslmo.api.v1.users.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.api.v1.users.models.BaseRegisterRequest
import com.sslmo.api.v1.users.models.EmailRegisterRequest
import com.sslmo.api.v1.users.models.SocialRegisterRequest
import com.sslmo.api.v1.users.models.UpdateAddressRequest
import com.sslmo.database.DatabaseFactory.dbQuery
import com.sslmo.database.tables.User
import com.sslmo.database.tables.Users
import com.sslmo.models.SignType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.and
import org.koin.core.annotation.Module
import java.util.*


@Module
class UserRepository {

	suspend fun findById(id: Int): User {
		return withContext(Dispatchers.IO) {
			dbQuery {
				User.findById(id)!!
			}
		}
	}

	suspend fun findBySocialId(socialId: String, signType: SignType): User? {
		return withContext(Dispatchers.IO) {
			dbQuery {
				User.find { Users.socialId eq socialId and (Users.type eq signType) }.firstOrNull()
			}
		}
	}

	suspend fun findByEmail(email: String): User? {
		return withContext(Dispatchers.IO) {
			dbQuery {
				val user = User.find { Users.email eq email and (Users.type eq SignType.EMAIL) }.firstOrNull()
				user
			}
		}
	}


	suspend fun findByNickname(nickname: String): User? {
		return withContext(Dispatchers.IO) {
			dbQuery {
				User.find { Users.nickname eq nickname }.firstOrNull()
			}
		}
	}

	suspend fun addNewUser(registerRequest: BaseRegisterRequest): Int {
		when (registerRequest) {
			is EmailRegisterRequest -> {
				val hashedPassword = BCrypt.withDefaults().hashToString(12, registerRequest.password.toCharArray())
				return withContext(Dispatchers.IO) {
					dbQuery {
						User.new {
							email = registerRequest.email
							password = hashedPassword
							nickname = registerRequest.nickName
							type = SignType.EMAIL
						}
					}.id.value
				}
			}

			is SocialRegisterRequest -> {
				return withContext(Dispatchers.IO) {
					dbQuery {
						User.new {
							email = registerRequest.email
							socialId = registerRequest.socialId
							nickname = registerRequest.nickName
							type = registerRequest.type
						}.id.value
					}
				}
			}

		}
	}

	suspend fun resetPassword(userId: UUID, hashedPassword: String): Boolean {

		return runCatching {
			withContext(Dispatchers.IO) {
				dbQuery {
					User.find { Users.uuid eq userId }.first().password = hashedPassword
				}
			}
		}.isSuccess
	}

	suspend fun updateAddress(userId: UUID, address: UpdateAddressRequest): Boolean {

		return runCatching {
			withContext(Dispatchers.IO) {
				dbQuery {
					User.find { Users.uuid eq userId }.first().let {
						it.siCode = address.siCode
						it.guCode = address.guCode
						it.dongCode = address.dongCode
					}
				}

			}
		}.isSuccess
	}
}