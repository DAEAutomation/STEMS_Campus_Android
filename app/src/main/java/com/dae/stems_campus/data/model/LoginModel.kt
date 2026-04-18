package com.dae.stems_campus.data.model

class LoginModel {
    data class LoginRequest(
        val username: String,
        val password: String,
        val uuid: String
    )

    data class LoginData(
        val user: UserData? = null,
        val accessToken: String? = null,
        val refreshToken: String? = null,
        val tokenExpiresIn: Int? = null,
        val mqtt: MqttData? = null
    )

    data class UserData(
        val id: Int? = null,
        val uid: String? = null,
        val studentId: String? = null,
        val name: String? = null,
        val role: String? = null,
        val balance: Double? = null,
        val hoursBalance: Int? = null,
        val canUse: Boolean? = null,
        val inDebt: Boolean? = null,
        val debtAmount: Int? = null
    )

    data class MqttData(
        val broker: String? = null,
        val port: Int? = null,
        val brokerInternal: String? = null,
        val portInternal: Int? = null,
        val username: String? = null,
        val password: String? = null,
        val topicPrefix: String? = null
    )

    //密碼驗證
    data class VerifyRequest(
        val password: String,
        val type: String,
        val device_id: String
    )

    data class VerifyData(
        val verifyToken: String? = null,
        val type: String? = null,
        val expiresAt: String? = null
    )

}