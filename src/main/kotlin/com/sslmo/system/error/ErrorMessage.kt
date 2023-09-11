package com.sslmo.system.error

import com.sslmo.models.SignType

object ErrorMessage {
	val DUPLICATE_NICKNAME = "이미 사용중인 닉네임입니다."
	val INVALID_PASSWORD = "비밀번호가 일치하지 않습니다."
	val NOT_FOUND_USER = "존재하지 않는 유저입니다."
	val USER_EXIST = "이미 존재 하는 유저입니다."
	val EMAIL_EXIST: (SignType) -> String = { type -> "이미 ${type}으로 가입하였습니다." }
	val BAD_REQUEST = "잘못된 요청입니다."
	val UNAUTHORIZED = "인증되지 않은 요청입니다."
}