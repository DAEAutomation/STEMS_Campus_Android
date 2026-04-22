package com.dae.stems_campus.data.model

class NotificationsModel {
    data class NotificationsData(
        val id: Int? = null,
        val eventType: String? = null,
        val title: String? = null,
        val message: String? = null,
        val isRead: Boolean? = null,
        val createdAt: String? = null
    )
}