package com.dae.stems_campus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.ServiceCheckRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import com.dae.stems_campus.network.BaseUrlHolder
import com.dae.stems_campus.network.SessionEventBus
import com.dae.stems_campus.utils.isVersionOutdated
import com.dae.stems_campus.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userPreferences: UserPreferencesRepository,
    private val baseUrlHolder: BaseUrlHolder,
    private val serviceCheckRepository: ServiceCheckRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // 任何 API 走到 Unauthorized 時 BaseRepository 會 emit；
        // 延 1.5 秒讓畫面端「請重新登入」dialog 先顯示完，再清 token 並同步 state。
        // 導航交由 MainActivity 的 AppContent 直接 collect 同一個 event 處理，
        // 因為登入流程目前沒有把 _authState 切到 Authenticated，靠 state 變化驅動會失效。
        viewModelScope.launch {
            SessionEventBus.forceLogout.collect {
                delay(1500)
                tokenManager.clearTokens()
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    /**
     * 啟動流程：baseUrl → serviceCheck → token check
     */
    fun checkToken() {
        viewModelScope.launch {
            try {
                // 1. 檢查是否已選擇學校（baseUrl 是否存在）
                val storedBaseUrl = userPreferences.getApiDomainValue.firstOrNull().orEmpty()
                if (storedBaseUrl.isEmpty()) {
                    _authState.value = AuthState.NeedSchoolSelection
                    return@launch
                }
                // 2. 把 baseUrl 灌進 holder
                baseUrlHolder.baseUrl = storedBaseUrl

                // 3. 服務狀態與版本檢查
                val serviceCheckResult = serviceCheckRepository.getServiceCheckData()
                if (serviceCheckResult is BaseRepository.Result.Success) {
                    val data = serviceCheckResult.data

                    // 服務維護中
                    if (data.serviceState == false) {
                        _authState.value = AuthState.ServiceUnavailable(
                            message = data.serviceMessage.orEmpty()
                        )
                        return@launch
                    }

                    // 強制更新檢查
                    val requiredVersion = data.appVersion.orEmpty()
                    val currentVersion = BuildConfig.VERSION_NAME
                    if (requiredVersion.isNotEmpty() &&
                        isVersionOutdated(currentVersion, requiredVersion)) {
                        _authState.value = AuthState.NeedAppUpdate(
                            currentVersion = currentVersion,
                            requiredVersion = requiredVersion
                        )
                        return@launch
                    }
                }
                // serviceCheck 失敗（網路/500 等）→ 視為通過，繼續往下

                // 4. token check
                val isValid = tokenManager.isTokenValid()
                _authState.value = if (isValid) {
                    AuthState.Authenticated
                } else {
                    AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                Log.e("DAE_Develop", "檢查啟動流程錯誤", e)
                _authState.value = AuthState.Unauthenticated
            }

        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object NeedSchoolSelection : AuthState()
    data class ServiceUnavailable(val message: String) : AuthState()
    data class NeedAppUpdate(val currentVersion: String, val requiredVersion: String) : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}
