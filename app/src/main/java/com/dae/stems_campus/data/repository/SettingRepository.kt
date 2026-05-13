package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.AccountModel
import com.dae.stems_campus.data.model.SettingModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class SettingRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    suspend fun cardBinding(aUID: String): Result<Unit> {
        return executeAuthenticatedRequestNoData {
            apiService.cardBinding(SettingModel.CardBindingRequest(
                uid = aUID
            ))
        }
    }
}