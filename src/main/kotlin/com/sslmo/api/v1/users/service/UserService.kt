package com.sslmo.api.v1.users.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.api.v1.users.models.*
import com.sslmo.api.v1.users.repository.UserRepository
import com.sslmo.models.SignType
import com.sslmo.system.error.DuplicateException
import com.sslmo.system.error.InValidPasswordException
import io.ktor.server.plugins.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Module
import java.util.*

@Module
class UserService(private val repository: UserRepository) {

	suspend fun emailLogin(email: String, password: String): UserModel? {
		val user = repository.findByEmail(email) ?: return null
		val passwordVeryfied = BCrypt.verifyer().verify(password.toCharArray(), user.password).verified
		if (!passwordVeryfied) throw InValidPasswordException()
		return user.toUserModel()
	}

	suspend fun socialLogin(socialId: String, signType: SignType): UserModel {
		return repository.findBySocialId(socialId, signType)?.toUserModel() ?: throw NotFoundException("존재하지 않는 유저입니다.")
	}

	suspend fun register(registerRequest: BaseRegisterRequest): UserModel {
		when (registerRequest) {
			is EmailRegisterRequest -> {
				repository.findByEmail(registerRequest.email)?.let {
					throw DuplicateException("이미 ${it.type}으로 가입한 이메일입니다.")
				} ?: run {
					repository.findByNickname(registerRequest.nickName)?.let {
						throw DuplicateException("이미 사용중인 닉네임입니다.")
					} ?: run {
						val id = repository.addNewUser(registerRequest)
						return repository.findById(id).toUserModel()
					}
				}
			}

			is SocialRegisterRequest -> {
				repository.findBySocialId(registerRequest.socialId, registerRequest.type)?.let {
					throw DuplicateException("이미 ${it.type}으로 가입한 이메일입니다.")
				} ?: run {
					repository.findByNickname(registerRequest.nickName)?.let {
						throw DuplicateException("이미 사용중인 닉네임입니다.")
					} ?: run {
						val id = repository.addNewUser(registerRequest)
						return repository.findById(id).toUserModel()
					}
				}
			}

		}
	}

	suspend fun checkEmailExist(email: String): Boolean {
		return repository.findByEmail(email) != null
	}

	suspend fun resetPassword(userId: UUID, password: String): Boolean {
		val hashedPassword = withContext(Dispatchers.Default) {
			BCrypt.withDefaults().hashToString(12, password.toCharArray())
		}
		return repository.resetPassword(userId, hashedPassword)
	}

	suspend fun updateAddress(userId: UUID, address: UpdateAddressRequest): Boolean =
		repository.updateAddress(userId, address)
}