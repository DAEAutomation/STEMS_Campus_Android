package com.dae.stems_campus.data.repository


import com.dae.stems_campus.data.model.PayModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class PayRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager) {

    suspend fun disbursement(walletId: Int, amount: Int): Result<PayModel.DisbursementData> {
        return executeAuthenticatedRequest {
            apiService.disbursement(
                PayModel.DisbursementRequest(
                    walletId = walletId,
                    amount = amount
                )
            )
        }
    }
}