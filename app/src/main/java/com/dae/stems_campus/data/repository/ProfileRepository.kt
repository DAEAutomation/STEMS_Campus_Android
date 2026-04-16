package com.dae.stems_campus.data.repository


import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class ProfileRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    suspend fun getProfileData(): Result<ProfileModel.ProfileData> {
        return executeAuthenticatedRequest {
            apiService.fetchProfileData()
        }
    }

    // 使用中裝置內容
    suspend fun getUsingDeviceDetail(aDeviceCode: String, aDeviceID: String): Result<ProfileModel.ActiveSession> {
        return executeAuthenticatedRequest {
            apiService.usingDeviceDetail(ProfileModel.UsingDeviceDetailRequest(
                    device_code = aDeviceCode,
                    device_id = aDeviceID
                )
            )
        }
    }
}