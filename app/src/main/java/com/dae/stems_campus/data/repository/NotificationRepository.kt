package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.data.model.NotificationsModel
import com.dae.stems_campus.data.model.RefundModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class NotificationRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    suspend fun getNotification(): Result<List<NotificationsModel.NotificationsData>> {
        return executeAuthenticatedRequest {
            apiService.getNotifications()
        }
    }
}