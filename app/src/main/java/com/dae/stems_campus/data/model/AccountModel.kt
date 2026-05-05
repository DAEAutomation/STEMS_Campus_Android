package com.dae.stems_campus.data.model

class AccountModel {

    data class SendEmailRequest(
        val email: String,
        val purpose: String
    )

    data class VerifyCodeRequest(
        val email: String,
        val code: String,
        val purpose: String
    )

    data class VerifyCodeData(
        val verifiedToken: String? = null,
        val email: String? = null,
        val expiresIn: Int? = null
    )

    data class GetStudentInfoRequest(
        val email: String,
        val student_id: String,
        val verified_token: String
    )

    data class StudentData(
        val student_id: String? = null,
        val name: String? = null,
        val phone: String? = null,
        val college: String? = null,
        val department: String? = null,
        val student_type: String? = null
    )

    data class RegisterRequest(
        val email: String,
        val student_id: String,
        val password: String,
        val verified_token: String
    )

    data class ResetPasswordRequest(
        val email: String,
        val password: String,
        val verified_token: String
    )
}