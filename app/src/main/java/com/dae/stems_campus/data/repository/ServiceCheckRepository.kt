package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.LoginModel
import com.dae.stems_campus.data.model.ServiceCheckModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class ServiceCheckRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    suspend fun getServiceCheckData(): Result<ServiceCheckModel.ServiceCheckData> {
        return executeRequest {
            apiService.serviceCheck(ServiceCheckModel.ServiceCheckRequest(
                app_id = "com.dae.stemscampus"
            ))
        }
    }
}