package com.dae.stems_campus.data.model

class RefundModel {

    // 申請退款 Request
    data class RefundRequest(
        val refund_type: Int,
    )

    // 申請退款
    data class RefundRequestData(
        val refundId: Int? = null,
        val refundNo: String? = null,
        val refundCode: String? = null,
        val refundType: Int? = null,
        val amount: Double? = null,
        val status: String? = null,
        val createdAt: String? = null
    )


    // 進行退款-悠遊卡 Request
    data class StartRefundRequest(
        val refund_id: Int,
        val deposit_code: String,
        val device_id: String
    )

    // 進行退款-悠遊卡
    data class StartRefundData(
        val refundId: Int? = null,
        val txCode: String? = null,
        val kioskName: String? = null,
        val depositMac: String? = null,
        val amount: Int? = null,
        val status: String? = null,
        val mqtt: TopUpModel.MqttInfo? = null
    )

    // 退款狀態查詢
    data class RefundStatusData(
        val refundId: Int? = null,
        val refundNo: String? = null,
        val refundCode: String? = null,
        val refundType: String? = null,
        val amount: Double? = null,
        val status: String? = null,
        val createdAt: String? = null,
        val completedAt: String? = null
    )

    // 退款取消 Request
    data class CancelRefundRequest(
        val refund_id: Int,
    )

    // 退款取消
    data class CancelRefundData(
        val refundId: Int? = null,
        val refundNo: String? = null,
        val status: String? = null
    )
}