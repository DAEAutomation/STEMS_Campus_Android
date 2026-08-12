package com.dae.stems_campus.data.model

class SettingModel {

    data class CardBindingRequest(
        val uid: String
    )

    //宿舍掃描資訊
    data class DormitoryScanRequest(
        val qr_token: String
    )

    data class DormScanInfoData(
        val roomId: Int? = null,
        val roomNumber: String? = null,
        val buildingName: String? = null,
        val floorName: String? = null,
        val memberCount: Int? = null,
        val memberMax: Int? = null,
        val alreadyBound: Boolean? = null,
        val boundOther: Boolean? = null
    )

    //宿舍綁定
    data class DormitoryBindingRequest(
        val qr_token: String
    )

    data class DormBindingData(
        val roomId: Int? = null,
        val roomNumber: String? = null,
        val buildingName: String? = null,
        val floorName: String? = null,
        val wallet: WalletDetail? = null,
        val members: List<Members>? = null,
        val memberCount: Int? = null,
        val memberMax: Int? = null
    )

    data class WalletDetail(
        val balance: Double? = null,
        val kwh: Double? = null,
        val rate: Double? = null,
        val max: Double? = null
    )

    data class Members(
        val name: String? = null,
        val uid: String? = null
    )

    //宿舍解綁
    data class DormitoryUnbindingRequest(
        val roomId: Int
    )

    data class DormUnbindingData(
        val unbound: Boolean? = null
    )

    //查詢目前綁哪個宿舍
    data class MyDormitoryData(
        val bound: Boolean? = null,
        val roomId: Int? = null,
        val roomNumber: String? = null,
        val buildingName: String? = null,
        val floorName: String? = null,
        val wallet: WalletDetail? = null,
        val members: List<Members>? = null,
        val memberCount: Int? = null,
        val memberMax: Int? = null
    )
}