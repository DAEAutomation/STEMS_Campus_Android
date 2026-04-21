package com.dae.stems_campus.data.model

import com.google.gson.annotations.SerializedName

data class SchoolHostModel(
    val id: Int? = null,
    @SerializedName("school_name")
    val schoolName: String? = null,
    val host: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)
