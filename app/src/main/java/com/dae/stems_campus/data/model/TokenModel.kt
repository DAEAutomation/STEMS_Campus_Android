package com.dae.stems_campus.data.model

class TokenModel {

    data class TokenData(
        val accessToken: String? = null,
        val refreshToken: String? = null,
        val tokenExpiresAt: String? = null,
        val refreshExpiresAt: String? = null
    )

    data class PushNotificationTokenRequest(
        val push_token: String,
        val platform: String
    )
}