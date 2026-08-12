package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.AccountModel
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.ScanModel
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

    // Dormitory Scan-宿舍綁定掃描取得資訊

    suspend fun dormScanInfo(qrCode: String): Result<SettingModel.DormScanInfoData> {
        return executeAuthenticatedRequest {
            apiService.dormScanInfo(SettingModel.DormitoryScanRequest(
                qr_token = qrCode
            ))
        }
    }

    // Dormitory Binding-宿舍綁定

    suspend fun dormBinding(qrCode: String): Result<SettingModel.DormBindingData> {
        return executeAuthenticatedRequest {
            apiService.dormBinding(SettingModel.DormitoryBindingRequest(
                qr_token = qrCode
            ))
        }
    }

    // Dormitory Unbinding-宿舍解除綁定

    suspend fun dormUnbinding(aRoomID: Int): Result<SettingModel.DormUnbindingData> {
        return executeAuthenticatedRequest {
            apiService.dormUnbinding(SettingModel.DormitoryUnbindingRequest(
                roomId = aRoomID
            ))
        }
    }

    // 查詢目前綁哪房

    suspend fun getMyDormitoryData(): Result<SettingModel.MyDormitoryData> {
        return executeAuthenticatedRequest {
            apiService.fetchMyDormitoryData()
        }
    }
}