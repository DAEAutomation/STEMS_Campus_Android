package com.dae.stems_campus.data.repository

import android.util.Log
import com.dae.stems_campus.data.model.APIResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.dae.stems_campus.network.ApiService
import com.dae.stems_campus.network.SessionEventBus
import com.dae.stems_campus.viewmodel.TokenManager
import retrofit2.HttpException

/**
 * Repository 基礎類別
 * 職責：
 * 1. 提供統一的 API 請求方法
 * 2. 自動處理 Token 過期和重試
 * 3. 統一錯誤處理
 */
open class BaseRepository @Inject constructor(
    protected val apiService: ApiService,
    private val tokenManager: TokenManager
) {

    /**
     * 執行需要認證的 API 請求
     * 自動處理 Token 過期和重試
     */
    protected suspend fun <T> executeAuthenticatedRequest(
        apiCall: suspend () -> APIResponse.ApiResponse<T>
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            repeat(2) { attempt ->
                try {
                    val response = apiCall()

                    return@withContext when {
                        response.success == true && response.data != null -> {
                            Result.Success(response.data)
                        }
                        else -> Result.Error("請求失敗")
                    }

                } catch (e: HttpException) {
                    val errorResponse = parseErrorResponse(e)
                    val errorCode = errorResponse?.error?.code
                    val errorMessage = errorResponse?.error?.message

                    // 處理 HTTP 401 Token 過期
                    if (e.code() == 401 && errorCode == "TOKEN_EXPIRED" && attempt == 0) {
                        Log.d("DAE_Develop", "Token 過期，嘗試刷新")
                        if (tokenManager.refreshToken() == true) {
                            Log.d("DAE_Develop", "Token 刷新成功，重試請求")
                            return@repeat  // 繼續下一次迴圈重試
                        } else {
                            Log.e("DAE_Develop", "Token 刷新失敗")
                            tokenManager.clearTokens()
                            SessionEventBus.notifyForceLogout()
                            return@withContext Result.Unauthorized
                        }
                    } else if (e.code() == 401 && errorCode == "TOKEN_EXPIRED") {
                        // 第二輪仍 TOKEN_EXPIRED → session 真的失效，強制登出
                        tokenManager.clearTokens()
                        SessionEventBus.notifyForceLogout()
                        return@withContext Result.Unauthorized
                    } else {
                        // 其他 401（如 INVALID_CREDENTIALS）→ 一般錯誤，不清 token、不強制登出
                        return@withContext Result.Error(errorMessage ?: "請求失敗 (${e.code()})")
                    }
                } catch (e: Exception) {
                    Log.e("DAE_Develop", "API 請求錯誤", e)
                    return@withContext Result.Error(e.message ?: "網路錯誤")
                }
            }

            Result.Error("請求失敗")
        }
    }

    /**
     * 執行需要認證的 API 請求，但無回傳data欄位
     * 自動處理 Token 過期和重試
     */
    protected suspend fun executeAuthenticatedRequestNoData(
        apiCall: suspend () -> APIResponse.ApiResponse<Unit>
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            repeat(2) { attempt ->
                try {
                    val response = apiCall()

                    return@withContext when {
                        response.success == true -> {
                            Result.Success(Unit)          // 只看 success，不管 data
                        }
                        else -> Result.Error(response.message ?: "請求失敗")
                    }
                } catch (e: HttpException) {
                    val errorResponse = parseErrorResponse(e)
                    val errorCode = errorResponse?.error?.code
                    val errorMessage = errorResponse?.error?.message

                    // 處理 HTTP 401 Token 過期
                    if (e.code() == 401 && errorCode == "TOKEN_EXPIRED" && attempt == 0) {
                        Log.d("DAE_Develop", "Token 過期，嘗試刷新")
                        if (tokenManager.refreshToken() == true) {
                            Log.d("DAE_Develop", "Token 刷新成功，重試請求")
                            return@repeat
                        } else {
                            Log.e("DAE_Develop", "Token 刷新失敗")
                            tokenManager.clearTokens()
                            SessionEventBus.notifyForceLogout()
                            return@withContext Result.Unauthorized
                        }
                    } else if (e.code() == 401 && errorCode == "TOKEN_EXPIRED") {
                        // 第二輪仍 TOKEN_EXPIRED → session 真的失效，強制登出
                        tokenManager.clearTokens()
                        SessionEventBus.notifyForceLogout()
                        return@withContext Result.Unauthorized
                    } else {
                        // 其他 401（如 INVALID_CREDENTIALS）→ 一般錯誤，不清 token、不強制登出
                        return@withContext Result.Error(errorMessage ?: "請求失敗 (${e.code()})")
                    }
                } catch (e: Exception) {
                    Log.e("DAE_Develop", "API 請求錯誤", e)
                    return@withContext Result.Error(e.message ?: "網路錯誤")
                }
            }
            Result.Error("請求失敗")
        }
    }

    /**
     * 執行不需要認證的 API 請求（登入等）
     */
    protected suspend fun <T> executeRequest(
        apiCall: suspend () -> APIResponse.ApiResponse<T>
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                when {
                    response.success == true && response.data != null -> {
                        Result.Success(response.data)
                    }
                    else -> {
                        Result.Error("請求失敗")
                    }
                }

            } catch (e: HttpException) {
                Log.e("DAE_Develop", "API 請求錯誤", e)
                val errorResponse = parseErrorResponse(e)
                Result.Error(errorResponse?.error?.message ?: "請求失敗 (${e.code()})")
            } catch (e: Exception) {
                Log.e("DAE_Develop", "API 請求錯誤", e)
                Result.Error(e.message ?: "網路錯誤")
            }
        }
    }

    /**
     * 執行不需要認證的 API 請求（註冊、忘記密碼）
     */
//    protected suspend fun executeRequestByRegister(
//        apiCall: suspend () -> RegisterModel.RegisterData
//    ): Result<Unit> {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = apiCall()
//
//                when {
//                    response.result == true -> {
//                        Result.Success(Unit)
//                    }
//                    else -> {
//                        Result.Error(response.message ?: "請求失敗")
//                    }
//                }
//
//            } catch (e: Exception) {
//                Log.e("BaseRepository", "API 請求錯誤", e)
//                Result.Error(e.message ?: "網路錯誤")
//            }
//        }
//    }

    /**
     * 從 HttpException 解析 errorBody（只讀一次）
     */
    private fun parseErrorResponse(e: HttpException): APIResponse.ErrorResponse? {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (errorBody != null) {
                Gson().fromJson(errorBody, APIResponse.ErrorResponse::class.java)
            } else {
                null
            }
        } catch (ex: Exception) {
            Log.e("DAE_Develop", "解析錯誤回應失敗", ex)
            null
        }
    }

    /**
     * 統一的結果封裝
     */
    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
        object Unauthorized : Result<Nothing>()
    }
}
