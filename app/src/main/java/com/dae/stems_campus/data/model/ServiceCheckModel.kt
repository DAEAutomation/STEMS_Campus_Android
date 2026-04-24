package com.dae.stems_campus.data.model

class ServiceCheckModel {

    // Request
    data class ServiceCheckRequest(
        val app_id: String,
    )

    data class ServiceCheckData(
        val serviceState: Boolean? = null,
        val serviceMessage: String? = null,
        val appVersion: String? = null
    )
}