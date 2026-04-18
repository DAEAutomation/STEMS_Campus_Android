package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.data.model.TopUpModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class TopUpRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    suspend fun scanDeposit(aDepositCode: String): Result<TopUpModel.ScanDepositData> {
        return executeAuthenticatedRequest {
            apiService.scanDeposit(TopUpModel.TopUpScanDepositRequest(
                depositCode = aDepositCode
            ))
        }
    }

    suspend fun startTopUp(aDepositCode: String, aDeviceCode: String, aDeviceID: String): Result<TopUpModel.StartTopUpData> {
        return executeAuthenticatedRequest {
            apiService.startTopUp(TopUpModel.StartTopUpRequest(
                depositCode = aDepositCode,
                device_code = aDeviceCode,
                device_id = aDeviceID
            ))
        }
    }
}