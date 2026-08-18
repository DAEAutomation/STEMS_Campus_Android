package com.dae.stems_campus.data.model

class ScanModel {

    data class ScanRequest(
        val qr_token: String,
        val device_id: String
    )

    // 掃描取得資訊
    data class ScanData(
        val spaceId: Int? = null,
        val spaceName: String? = null,
        val spaceType: String? = null,
        val buildingName: String? = null,
        val floorName: String? = null,
        val deviceCode: String? = null,
        val channelId: Int? = null,
        val billingState: String? = null,
        val energy: String? = null,
        val relayStatus: String? = null,
        val usageStatus: Int? = null,
        val rate: Double? = null,
        val yourBalance: Double? = null,
        val yourHoursBalance: Int? = null,
        val canStart: Boolean? = null,
        val canStartReason: String? = null,
        val isAcOpenPeriod: Boolean? = null,
        val freeMode: Boolean? = null,
        val sessionToken: String? = null,
        val sessionTokenExpiresAt: String? = null,
        val bound: Boolean? = null,
        val room: Room? = null,
        val walletOptions: List<WalletOptions>? = null,
        var activeSession: ActiveSessionByCurrent? = null,
    )

    // 啟用用電:教室 Request
    data class StartClassroomPowerRequest(
        val device_code: String,
        val device_id: String,
        val session_token: String,
    )

    // 啟用用電:宿舍 Request
    data class StartDormitoryPowerRequest(
        val device_code: String,
        val device_id: String,
        val walletId: Int,
        val session_token: String,
    )

    // 啟用用電+冷氣 :教室 Request
    data class StartClassroomPowerByAcRequest(
        val device_code: String,
        val device_id: String,
        val session_token: String,
        val ac: Boolean
    )

    // 啟用用電
    data class StartPowerData(
        val sessionId: Int? = null,
        val deviceCode: String? = null,
        val spaceName: String? = null,
        val balance: Double? = null,
        val state: String? = null
    )

    // 停止用電 Request
    data class StopPowerRequest(
        val device_code: String,
        val device_id: String,
        val control_token: String,
    )

    // 停止用電
    data class StopPowerData(
        val session: StopSessionData? = null,
        val state: String? = null,
        val balance: Double? = null,
        val totalCharged: Double? = null
    )

    data class StopSessionData(
        val id: Int? = null,
        val userId: Int? = null,
        val deviceId: Int? = null,
        val spaceId: Int? = null,
        val startTime: String? = null,
        val endTime: String? = null,
        val startMeter: Double? = null,
        val endMeter: Double? = null,
        val totalKwh: Double? = null,
        val totalAmount: Double? = null,
        val state: String? = null,
        val endReason: String? = null,
        val createdAt: String? = null,
        val updatedAt: String? = null,
        val deletedAt: String? = null,
        val acStartTime: String? = null,
        val acEndTime: String? = null,
        val acTotalKwh: Double? = null,
        val acTotalAmount: Double? = null
    )

    // 冷氣控制 Request
    data class ControlRequest(
        val device_id: String,
        val control_token: String,
        val command: String
    )

    // 冷氣控制
    data class ControlData(
        val command: String? = null,
        val executed: Boolean? = null,
        val message: String? = null
    )

    data class Room(
        val roomId: Int? = null,
        val roomNumber: String? = null,
        val memberMax: Int? = null
    )

    data class WalletOptions(
        val type: String? = null,
        val walletId: Int? = null,
        val name: String? = null,
        val balance: Double? = null,
        val kwh: Double? = null,
        val available: Boolean? = null
    )

    data class ActiveSessionByCurrent(
        val type: String? = null,
        val mine: Boolean? = null,
        val startedBy: StartedBy? = null
    )

    data class StartedBy(
        val name: String? = null,
        val uid: String? = null
    )
}