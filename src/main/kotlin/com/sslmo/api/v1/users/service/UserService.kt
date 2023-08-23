package com.sslmo.api.v1.users.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.sslmo.api.v1.users.repository.UserRepository
import com.sslmo.models.SignType
import com.sslmo.models.user.BaseRegisterRequest
import com.sslmo.models.user.EmailRegisterRequest
import com.sslmo.models.user.SocialRegisterRequest
import com.sslmo.models.user.User
import com.sslmo.system.error.DuplicateException
import com.sslmo.system.error.InValidPasswordException
import io.ktor.server.plugins.*
import org.koin.core.annotation.Module
import org.koin.java.KoinJavaComponent.inject

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
}