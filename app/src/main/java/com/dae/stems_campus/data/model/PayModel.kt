package com.dae.stems_campus.data.model

class PayModel {
    data class DisbursementRequest(
        val walletId: Int,
        val amount: Int
    )

    data class DisbursementData(
        val personalBalance: Double? = null,
        val dormWallet: DormWallet? = null,
        val serialNo: String? = null
    )

    data class DormWallet(
        val balance: Double? = null,
        val kwh: Double? = null,
        val rate: Double? = null,
        val max: Double? = null
    )
}