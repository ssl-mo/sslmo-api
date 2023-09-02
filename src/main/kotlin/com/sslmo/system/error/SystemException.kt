package com.sslmo.system.error


class InValidPasswordException : RuntimeException("비밀번호가 일치하지 않습니다.")

class DuplicateException(override val message: String) : RuntimeException(message)