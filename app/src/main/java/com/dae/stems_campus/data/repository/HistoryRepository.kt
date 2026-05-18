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

    // 時數發給紀錄
    suspend fun getHoursHistory(aStartDate: String, aEndDate: String): Result<List<HistoryModel.HoursHistory>> {
        return executeAuthenticatedRequest {
            apiService.hoursHistory(HistoryModel.HistoryRequest(
                start_date = aStartDate,
                end_date = aEndDate
            ))
        }
    }

    // 教室使用紀錄
    suspend fun getClassroomHistory(aStartDate: String, aEndDate: String): Result<List<HistoryModel.ClassroomHistory>> {
        return executeAuthenticatedRequest {
            apiService.classroomHistory(HistoryModel.HistoryRequest(
                start_date = aStartDate,
                end_date = aEndDate
            ))
        }
    }

    // 宿舍使用紀錄
    suspend fun getDormitoryHistory(aStartDate: String, aEndDate: String): Result<List<HistoryModel.DormitoryHistory>> {
        return executeAuthenticatedRequest {
            apiService.dormitoryHistory(HistoryModel.HistoryRequest(
                start_date = aStartDate,
                end_date = aEndDate
            ))
        }
    }

    // 退款紀錄
    suspend fun getRefundHistory(aStartDate: String, aEndDate: String): Result<List<HistoryModel.RefundHistory>> {
        return executeAuthenticatedRequest {
            apiService.refundHistory(HistoryModel.HistoryRequest(
                start_date = aStartDate,
                end_date = aEndDate
            ))
        }
    }

    // 退款紀錄明細下載
    suspend fun getRefundHistoryDownload(aRefundID: Int): Result<HistoryModel.RefundHistoryDownload> {
        return executeAuthenticatedRequest {
            apiService.refundHistoryDownload(HistoryModel.RefundHistoryDownloadRequest(
                refund_id = aRefundID
            ))
        }
    }
}