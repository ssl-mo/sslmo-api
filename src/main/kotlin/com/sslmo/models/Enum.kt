package com.sslmo.models

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

enum class AuthorizedType {
    APP,
    ACCESS,
    REFRESH,
}
