package com.dae.stems_campus.data.model

class HistoryModel {

    data class HistoryTypeItem(
        val titleRes: Int,
        val route: String
    )

    data class HistoryRequest(
        val start_date: String,
        val end_date: String
    )

    // 錢包儲值紀錄
    data class WalletHistory(
        val id: Int? = null,
        val amount: Int? = null,
        val balanceBefore: Double? = null,
        val balanceAfter: Double? = null,
        val paymentMethod: String? = null,
        val note: String? = null,
        val createdAt: String? = null,
        val topupSerial: String? = null,
        val account: String? = null,
        val userName: String? = null,
        val kioskName: String? = null,
        val txStatus: String? = null,
        val kioskPaymentType: Int? = null,
        val ezDeviceId: String? = null,
        val ezCardId: String? = null,
        val kioskPaymentTypeLabel: String? = null,
        val ezBalanceBefore: Int? = null,
        val ezBalanceAfter: Int? = null,
    )

    data class Pagination(
        val total: Int? = null,
        val page: Int? = null,
        val limit: Int? = null
    )

    // 時數發給紀錄
    data class HoursHistory(
        val id: Int? = null,
        val hours: Int? = null,
        val hoursBefore: Int? = null,
        val hoursAfter: Int? = null,
        val note: String? = null,
        val createdAt: String? = null
    )

    // 教室使用紀錄
    data class ClassroomHistory(
        val source: String? = null,
        val id: Int? = null,
        val startTime: String? = null,
        val endTime: String? = null,
        val totalKwh: Double? = null,
        val totalAmount: Double? = null,
        val state: String? = null,
        val endReason: String? = null,
        val spaceName: String? = null,
        val recordType: String? = null,
        val usingTime: String? = null,
        val buildingName: String? = null,
        val general: General? = null,
        val ac: Ac? = null,
        val durationMinutes: Int? = null
    )

    data class General(
        val startTime: String? = null,
        val endTime: String? = null,
        val durationMinutes: Int? = null,
        val rate: Double? = null,
        val totalAmount: Double? = null
    )

    data class Ac(
        val startTime: String? = null,
        val endTime: String? = null,
        val durationMinutes: Int? = null,
        val rate: Double? = null,
        val totalAmount: Double? = null
    )

    // 宿舍使用紀錄
    data class DormitoryHistory(
        val sessionId: Int? = null,
        val startTime: String? = null,
        val endTime: String? = null,
        val totalKwh: Double? = null,
        val totalAmount: Double? = null,
        val state: String? = null,
        val endReason: String? = null,
        val startMeter: Double? = null,
        val endMeter: Double? = null,
        val spaceName: String? = null,
        val buildingName: String? = null,
        val rate: Double? = null,
        val powerType: String? = null,
        val walletType: String? = null,
        val walletLabel: String? = null,
        val walletName: String? = null,
        val startUser: String? = null,
        val stopUser: String? = null
    )

    //退款紀錄
    data class RefundHistory(
        val id: Int? = null,
        val refundNo: String? = null,
        val refundType: Int? = null,
        val refundTypeLabel: String? = null,
        val amount: Double? = null,
        val refundCode: String? = null,
        val status: Int? = null,
        val statusLabel: String? = null,
        val kioskName: String? = null,
        val balanceBefore: Double? = null,
        val balanceAfter: Double? = null,
        val transactionId: Int? = null,
        val createdAt: String? = null,
        val completedAt: String? = null,
        val updatedAt: String? = null,
        val applicantName: String? = null,
        val applicantNo: String? = null,
        val applicantRole: String? = null,
        val applicationDate: String? = null,
        val transactionNo: String? = null,
        val ezDeviceId: String? = null,
        val ezCardId: String? = null,
        val ezBalanceBefore: Int? = null,
        val ezBalanceAfter: Int? = null,
    )

    data class DisbursementHistory(
        val serialNo: String? = null,
        val createdAt: String? = null,
        val roomNumber: String? = null,
        val payerName: String? = null,
        val payerEmail: String? = null,
        val amount: Double? = null,
        val dormBefore: Double? = null,
        val dormAfter: Double? = null,
        val personalBalanceAfter: Double? = null,
        val status: String? = null,
        val walletLabel: String? = null,
        val failReason: String? = null,
        val grantedBy: String? = null
    )

    data class RefundHistoryDownloadRequest(
        val refund_id: Int
    )

    //退款紀錄明細下載
    data class RefundHistoryDownload(
        val token: String? = null,
        val download_url: String? = null,
        val download_name: String? = null,
        val expires_in: Double? = null
    )

    data class TopUpHistoryDownloadRequest(
        val transaction_id: Int
    )

    //儲值紀錄下載明細
    data class TopUpHistoryDownload(
        val token: String? = null,
        val download_url: String? = null,
        val download_name: String? = null,
        val expires_in: Double? = null
    )
}