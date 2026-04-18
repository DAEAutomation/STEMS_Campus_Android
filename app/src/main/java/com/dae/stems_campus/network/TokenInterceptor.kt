package com.dae.stems_campus.network

import android.util.Log
import com.dae.stems_campus.viewmodel.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 取得 Access Token
        val accessToken = tokenManager.getAccessToken()

        // 如果有 Token，就加到 Header
        val requestWithToken = if (accessToken != null) {
            Log.d("DAE_Develop", "accessToken -->$accessToken")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        // 直接執行請求，不處理任何業務邏輯
        return chain.proceed(requestWithToken)
    }
}