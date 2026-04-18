package com.dae.stems_campus.data.model

class TopUpModel {

    // 掃描儲值機 Request
    data class TopUpScanDepositRequest(
        val depositCode: String
    )

    // 掃描儲值機
    data class ScanDepositData(
        val kiosk: KioskData? = null,
        val canDeposit: Boolean? = null,
        val reason: String? = null,
        val mqtt: Mqtt? = null
    )

    data class KioskData(
        val name: String? = null,
        val kioskCode: String? = null,
        val mac: String? = null,
        val online: Boolean? = null
    )

    data class Mqtt(
        val hostUuid: String? = null,
        val depositMac: String? = null
    )

    // 發起儲值 Request
    data class StartTopUpRequest(
        val depositCode: String,
        val device_code: String,
        val device_id: String
    )

    // 發起儲值
    data class StartTopUpData(
        val txCode: String? = null,
        val depositName: String? = null,
        val depositMac: String? = null,
        val displayName: String? = null,
        val rate: Double? = null,
        val maxCreditAmount: Int? = null,
        val mqtt: MqttInfo? = null
    )

    data class MqttInfo(
        val topic: String? = null,
        val appId: String? = null
    )
}