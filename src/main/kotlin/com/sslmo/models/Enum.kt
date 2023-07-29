package com.sslmo.models

import kotlinx.serialization.Serializable


@Serializable
enum class SignType {
    EMAIL,
    GOOGLE,
    APPLE,
    NAVER,
    KAKAO;
}


enum class AppMode {
    LOCAL,
    DEV,
    PROD;
}

//enum class AuthorizedType {
//    APP,
//    ACCESS,
//    REFRESH,
//}

enum class TokenType {
    ACCESS,
    REFRESH,
}