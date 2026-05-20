package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.TokenModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class PushNotificationRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    suspend fun sendFCMToken(aFCMToken: String): Result<Unit> {
        return executeAuthenticatedRequestNoData {
            apiService.sendFCMToken(TokenModel.PushNotificationTokenRequest(
                push_token = aFCMToken,
                platform = "Android"
            ))
        }
    }
}