package com.dae.stems_campus.data.model

class ProfileModel {
    data class ProfileData(
        val id: Int? = null,
        val userId: String? = null,
        val uid: String? = null,
        val studentId: String? = null,
        val name: String? = null,
        val role: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val canUse: Boolean? = null,
        val isSuspended: Boolean? = null,
        val inDebt: Boolean? = null,
        val debtAmount: Double? = null,
        val registrationSource: String? = null,
        val createdAt: String? = null,
        val updatedAt: String? = null,
        val cardBinding: CardBinding? = null,
        val activeSession: ActiveSession? = null,
        val balance: Double? = null,
        val hoursBalance: Int? = null,
        val department: String? = null,
        val departmentName: String? = null,
        val college: String? = null,
        val jobTitle: String? = null,
        val studentType: String? = null,
        val pointsExpiresAt: String? = null,
        val hoursExpiresAt: String? = null,
        val hoursExpireDetail: List<HoursExpireDetail>? = null
    )

    data class HoursExpireDetail(
        val expiresAt: String? = null,
        val hours: Int? = null
    )

    data class CardBinding(
        val uid: String? = null,
        val isActive: Boolean? = null
    )

    data class ActiveSession(
        val hasActive: Boolean? = null,
        val user: UserTopUp? = null,
        val sessions: List<UsingDeviceData>? = null
    )

    data class UserTopUp(
        val balance: Double? = null,
        val hoursBalance: Int? = null
    )

    data class UsingDeviceData(
        val session: SessionDetail? = null,
        val space: SpaceDetail? = null,
        val device: DeviceDetail? = null,
        val billing: BillingDetail? = null,
        val control: ControlDetail? = null
    )

    data class SessionDetail(
        val sessionId: Int? = null,
        val state: String? = null,
        val source: String? = null,
        val createdAt: String? = null
    )

    data class SpaceDetail(
        val spaceId: Int? = null,
        val spaceName: String? = null,
        val spaceType: String? = null,
        val floorName: String? = null,
        val buildingName: String? = null
    )

    data class DeviceDetail(
        val deviceCode: String? = null,
        val channelId: Int? = null,
        val billingState: String? = null,
        val relayStatus: String? = null,
        val converterStatus: String? = null,
        val icrMode: Int? = null,
        val powerSupply: PowerSupplyData? = null
    )

    data class PowerSupplyData(
        val light: PowerSupplyDetail? = null,
        val socket: PowerSupplyDetail? = null,
        val ac: PowerSupplyDetail? = null
    )

    data class PowerSupplyDetail(
        val on: Boolean? = null,
        val remainingSec: Int? = null
    )

    data class BillingDetail(
        val rate: Double? = null,
        val powerMode: String? = null,
        val powerModeLabel: String? = null,
        val chargeUnit: String? = null,
        val userRole: String? = null,
        val general: GeneralDetail? = null,
        val ac: AcDetail? = null,
        val totalCharged: Double? = null,
        val totalKwh: Double? = null
    )

    data class GeneralDetail(
        val startTime: String? = null,
        val durationMinutes: Int? = null,
        val totalKwh: Double? = null,
        val totalAmount: Double? = null,
        val startMeter: Double? = null,
        val currentMeter: Double? = null
    )

    data class AcDetail(
        val startTime: String? = null,
        val durationMinutes: Int? = null,
        val totalKwh: Double? = null,
        val totalAmount: Double? = null
    )

    data class ControlDetail(
        val controlToken: String? = null,
        val controlTokenExpiresAt: String? = null,
        val availableCommands: List<String>? = null
    )

    // 使用中裝置內容 Request
    data class UsingDeviceDetailRequest(
        val device_code: String,
        val device_id: String,
    )
}