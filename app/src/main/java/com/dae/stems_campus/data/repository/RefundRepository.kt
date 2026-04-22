package com.dae.stems_campus.data.repository

import com.dae.stems_campus.data.model.RefundModel
import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.data.model.TopUpModel
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.viewmodel.TokenManager
import javax.inject.Inject

open class RefundRepository @Inject constructor(apiService: ApiService, tokenManager: TokenManager): BaseRepository(apiService, tokenManager){

    // 申請退款
    suspend fun refundRequest(aRefundType: Int): Result<RefundModel.RefundRequestData> {
        return executeAuthenticatedRequest {
            apiService.refundRequest(RefundModel.RefundRequest(
                refund_type = aRefundType
            ))
        }
    }

    // 進行退款-悠遊卡
    suspend fun startRefund(aRefundID: Int, aDeviceID: String, aDepositCode: String): Result<RefundModel.StartRefundData> {
        return executeAuthenticatedRequest {
            apiService.startRefund(RefundModel.StartRefundRequest(
                refund_id = aRefundID,
                device_id = aDeviceID,
                deposit_code = aDepositCode
            ))
        }
    }

    // 退款狀態查詢
    suspend fun fetchRefundStatus(): Result<RefundModel.RefundStatusData> {
        return executeAuthenticatedRequest {
            apiService.refundStatus()
        }
    }

    // 退款取消
    suspend fun cancelRefund(aRefundID: Int): Result<RefundModel.CancelRefundData> {
        return executeAuthenticatedRequest {
            apiService.cancelRefund(RefundModel.CancelRefundRequest(
                refund_id = aRefundID
            ))
        }
    }

}