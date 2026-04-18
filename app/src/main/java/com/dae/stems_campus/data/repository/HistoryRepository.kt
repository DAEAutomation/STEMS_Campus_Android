package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class HistoryRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    // 錢包儲值紀錄
    suspend fun getWalletHistory(aStartDate: String, aEndDate: String): Result<List<HistoryModel.WalletHistory>> {
        return executeAuthenticatedRequest {
            apiService.walletHistory(HistoryModel.HistoryRequest(
                start_date = aStartDate,
                end_date = aEndDate
            ))
        }
    }
}