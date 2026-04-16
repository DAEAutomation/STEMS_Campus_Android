package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.LoginModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class LoginRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    suspend fun getLoginData(aUserName: String, aPw: String, aUUID: String): Result<LoginModel.LoginData> {
        return executeRequest {
            apiService.login(LoginModel.LoginRequest(
                username = aUserName,
                password = aPw,
                uuid = aUUID
            ))
        }
    }

    suspend fun verifyPassword(aPassword: String, aType: String, aDeviceID: String): Result<LoginModel.VerifyData> {
        return executeAuthenticatedRequest {
            apiService.verifyPassword(LoginModel.VerifyRequest(
                password = aPassword,
                type = aType,
                device_id = aDeviceID
            ))
        }
    }
}