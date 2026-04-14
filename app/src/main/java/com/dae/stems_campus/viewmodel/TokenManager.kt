package com.dae.stems_campus.viewmodel

import android.util.Log
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class TokenManager @Inject constructor(private val credentialRepository: CredentialRepository, private val apiServiceLazy: dagger.Lazy<ApiService>) {

    // 延遲取得 ApiService
    private val apiService: ApiService
        get() = apiServiceLazy.get()

    private val tokenRefreshLock = Mutex()

    fun getEmail(): String? {
        return  credentialRepository.getUsername()
    }

    // 取得 Access Token
    fun getAccessToken(): String? {
        return credentialRepository.getAccessToken()
    }

    // 取得 Refresh Token
    fun getRefreshToken(): String? {
        return credentialRepository.getRefreshToken()
    }

    // 儲存新的 Token
    fun saveTokens(accessToken: String, refreshToken: String) {
        credentialRepository.saveTokenCredentials(accessToken, refreshToken)
    }

    // 儲存新的 Access Token
    fun saveAccessTokens(accessToken: String) {
        credentialRepository.saveAccessTokenCredentials(accessToken)
    }

    // 清除 Token（登出時使用）
    fun clearTokens() {
        credentialRepository.deleteToken()
    }

    /**
     * 刷新 Token
     * 使用 Mutex 確保同時只有一個請求在刷新 Token
     */
    suspend fun refreshToken(): Boolean? = withContext(Dispatchers.IO) {
        tokenRefreshLock.withLock {
            try {
                val refreshToken = getRefreshToken() ?: return@withLock null
                val email = getEmail() ?: return@withLock  null

                Log.d("DAE_Develop", "開始刷新 Token")

                // 呼叫你的 refresh token API
                val response = apiService.refreshToken()


                if (response.success == true && response.data != null) {
                    val newAccessToken = response.data.accessToken
                    saveAccessTokens(newAccessToken ?: "")
                    return@withLock true
                }else{
                    Log.e("DAE_Develop", "Token 刷新失敗")
                    return@withLock false
                }
            } catch (e: Exception) {
                Log.e("DAE_Develop", "Token 刷新錯誤", e)
                return@withLock false
            }
        }
    }

    /**
     * 檢查 Token 是否有效
     * 如果無效會嘗試刷新
     */
    suspend fun isTokenValid(): Boolean = withContext(Dispatchers.IO) {
        val accessToken = getAccessToken()

        // 沒有 token
        if (accessToken.isNullOrEmpty()) {
            Log.d("DAE_Develop", "沒有 Access Token")
            return@withContext false
        }

        // 可選：解析 JWT 檢查過期時間
        // 如果你的 token 是 JWT，可以解析檢查
        // 這裡先簡單嘗試刷新

        val refreshToken = getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            Log.d("DAE_Develop", "沒有 Refresh Token")
            return@withContext false
        }

        // 嘗試刷新 token
        val refreshResult = refreshToken()

        when (refreshResult) {
            true -> {
                Log.d("DAE_Develop", "Token 刷新成功")
                true
            }
            false -> {
                Log.d("DAE_Develop", "Token 刷新失敗，需要重新登入")
                clearTokens()
                false
            }
            null -> {
                Log.d("DAE_Develop", "Token 資料不完整")
                clearTokens()
                false
            }
        }
    }
}