package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.AccountModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class AccountRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    suspend fun registerSendEmail(aEmail: String): Result<Unit> {
        return executeAuthenticatedRequestNoData {
            apiService.registerSendEmail(AccountModel.SendEmailRequest(
                email = aEmail,
                purpose = "register"
            ))
        }
    }

    suspend fun registerVerifyCode(aEmail: String, aCode: String): Result<AccountModel.VerifyCodeData> {
        return executeAuthenticatedRequest {
            apiService.registerVerifyCode(AccountModel.VerifyCodeRequest(
                email = aEmail,
                code = aCode,
                purpose = "register"
            ))
        }
    }

    suspend fun getStudentInfo(aEmail: String, aStudentID: String, aToken: String): Result<AccountModel.StudentData> {
        return executeAuthenticatedRequest {
            apiService.getStudentInfo(AccountModel.GetStudentInfoRequest(
                email = aEmail,
                student_id = aStudentID,
                verified_token = aToken
            ))
        }
    }

    suspend fun registerInfo(aEmail: String, aStudentID: String, aPassword: String, aToken: String): Result<Unit> {
        return executeAuthenticatedRequestNoData {
            apiService.registerInfo(AccountModel.RegisterRequest(
                email = aEmail,
                student_id = aStudentID,
                password = aPassword,
                verified_token = aToken
            ))
        }
    }

}