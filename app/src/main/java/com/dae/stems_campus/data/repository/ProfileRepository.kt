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
}