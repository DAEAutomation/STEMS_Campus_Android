package com.dae.stems_campus.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * 回應日誌攔截器
 * 印出原始的 HTTP 回應內容
 */
class ResponseLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        Log.d("API_REQUEST", "========================================")
        Log.d("API_REQUEST", "URL: ${request.url}")
        Log.d("API_REQUEST", "Method: ${request.method}")
        Log.d("API_REQUEST", "Headers: ${request.headers}")

        // 讀取請求 Body
        request.body?.let { requestBody ->
            val buffer = okio.Buffer()
            requestBody.writeTo(buffer)
            val requestBodyString = buffer.readUtf8()
            Log.d("API_REQUEST", "Body: $requestBodyString")
        }
        Log.d("API_REQUEST", "========================================")


        // 執行請求
        val response = chain.proceed(request)

        // 讀取回應內容
        val responseBody = response.body
        val responseBodyString = responseBody?.string()

        Log.d("API_RESPONSE", "========================================")
        Log.d("API_RESPONSE", "URL: ${response.request.url}")
        Log.d("API_RESPONSE", "Status Code: ${response.code}")
        Log.d("API_RESPONSE", "Message: ${response.message}")
        Log.d("API_RESPONSE", "Headers: ${response.headers}")
        Log.d("API_RESPONSE", "========================================")
        Log.d("API_RESPONSE", "Body: $responseBodyString")
        Log.d("API_RESPONSE", "========================================")

        // 重新建立 Response（因為 ResponseBody 只能讀一次）
        val newResponseBody = responseBodyString?.toResponseBody(responseBody.contentType())

        return response.newBuilder()
            .body(newResponseBody)
            .build()
    }
}