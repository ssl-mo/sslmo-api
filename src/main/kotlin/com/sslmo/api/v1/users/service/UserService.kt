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
import org.koin.java.KoinJavaComponent.inject
import java.util.*

@Module
class UserService {

	private val userRepository by inject<UserRepository>(clazz = UserRepository::class.java)

	suspend fun emailLogin(email: String, password: String): User? {
		val user = userRepository.findByEmail(email) ?: return null
		val passwordVeryfied = BCrypt.verifyer().verify(password.toCharArray(), user.password).verified
		if (!passwordVeryfied) throw InValidPasswordException()
		return user
	}

	suspend fun socialLogin(socialId: String, signType: SignType): User {
		return userRepository.findBySocialId(socialId, signType) ?: throw NotFoundException("존재하지 않는 유저입니다.")
	}

	suspend fun register(registerRequest: BaseRegisterRequest): User {
		when (registerRequest) {
			is EmailRegisterRequest -> {
				userRepository.findByEmail(registerRequest.email)?.let {
					throw DuplicateException("이미 ${it.type}으로 가입한 이메일입니다.")
				} ?: run {
					userRepository.findByNickname(registerRequest.nickName)?.let {
						throw DuplicateException("이미 사용중인 닉네임입니다.")
					} ?: run {
						val id = userRepository.addNewUser(registerRequest)
						return userRepository.findById(id)
					}
				}
			}

			is SocialRegisterRequest -> {
				userRepository.findBySocialId(registerRequest.socialId, registerRequest.type)?.let {
					throw DuplicateException("이미 ${it.type}으로 가입한 이메일입니다.")
				} ?: run {
					userRepository.findByNickname(registerRequest.nickName)?.let {
						throw DuplicateException("이미 사용중인 닉네임입니다.")
					} ?: run {
						val id = userRepository.addNewUser(registerRequest)
						return userRepository.findById(id)
					}
				}
			}

		}
	}

	suspend fun checkEmailExist(email: String): Boolean {
		return userRepository.findByEmail(email) != null
	}

	suspend fun resetPassword(userId: UUID, password: String): Boolean {
		val hashedPassword = withContext(Dispatchers.Default) {
			BCrypt.withDefaults().hashToString(12, password.toCharArray())
		}
		return userRepository.resetPassword(userId, hashedPassword)
	}

	suspend fun updateAddresss(userId: UUID, address: UpdateAddresssRequest): Boolean =
		userRepository.updateAddresss(userId, address)
}