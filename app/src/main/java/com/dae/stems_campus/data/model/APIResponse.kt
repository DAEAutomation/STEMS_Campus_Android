package com.dae.stems_campus.data.model

class APIResponse {
    // 成功時的回應
    data class ApiResponse<T>(
        val success: Boolean?,
        val data: T?
    )

    // 錯誤時的回應結構
    data class ErrorResponse(
        val success: Boolean?,
        val error: ErrorInfo?
    )

    data class ErrorInfo(
        val code: String?,
        val message: String?
    )
}