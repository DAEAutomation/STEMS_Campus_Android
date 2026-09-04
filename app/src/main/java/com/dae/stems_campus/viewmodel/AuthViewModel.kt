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

    // 登入前檢查期間的 loading，給登入頁的按鈕用
    private val _checkingBeforeLogin = MutableStateFlow(false)
    val checkingBeforeLogin: StateFlow<Boolean> = _checkingBeforeLogin.asStateFlow()

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
                if (!passServiceAndVersionCheck()) return@launch

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

    /**
     * 服務維護與強制更新檢查。
     * 回傳 true 表示可繼續；false 表示已把 _authState 切到攔截畫面。
     * serviceCheck 失敗（網路/500 等）一律回 true，維持既有的 fail-open 策略。
     */
    private suspend fun passServiceAndVersionCheck(): Boolean {
        val serviceCheckResult = serviceCheckRepository.getServiceCheckData()
        if (serviceCheckResult !is BaseRepository.Result.Success) return true

        val data = serviceCheckResult.data

        // 服務維護中
        if (data.serviceState == false) {
            _authState.value = AuthState.ServiceUnavailable(
                message = data.serviceMessage.orEmpty(),
                endTime = data.serviceEndTime.orEmpty()
            )
            return false
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
            return false
        }

        return true
    }

    /**
     * 登入前檢查：服務維護中或需強制更新就切到對應畫面，onPass 不執行、登入請求不送出。
     * 補的是輪詢的空窗期——維護開始到下一次輪詢之間，使用者仍停在登入頁的那段時間。
     * 不碰 token，避免本機殘留的有效 token 讓使用者繞過登入。
     */
    fun checkBeforeLogin(onPass: () -> Unit) {
        viewModelScope.launch {
            _checkingBeforeLogin.value = true
            try {
                val storedBaseUrl = userPreferences.getApiDomainValue.firstOrNull().orEmpty()
                if (storedBaseUrl.isEmpty()) {
                    onPass()
                    return@launch
                }
                baseUrlHolder.baseUrl = storedBaseUrl

                if (passServiceAndVersionCheck()) onPass()
            } catch (e: Exception) {
                Log.e("DAE_Develop", "登入前檢查錯誤", e)
                onPass()
            } finally {
                _checkingBeforeLogin.value = false
            }
        }
    }

    /**
     * 只重查服務維護狀態，不碰 token。
     * 給「切回前景」與「停在維護畫面時輪詢」用；
     * 刻意不複用 checkToken()，避免每次回前景都重驗 token 把使用者踢回登入頁。
     */
    fun recheckServiceState() {
        viewModelScope.launch {
            try {
                val storedBaseUrl = userPreferences.getApiDomainValue.firstOrNull().orEmpty()
                if (storedBaseUrl.isEmpty()) return@launch
                baseUrlHolder.baseUrl = storedBaseUrl

                val result = serviceCheckRepository.getServiceCheckData()
                if (result !is BaseRepository.Result.Success) return@launch

                val data = result.data
                when {
                    // 維護中 → 不管目前在哪一頁都切到維護畫面
                    data.serviceState == false -> {
                        _authState.value = AuthState.ServiceUnavailable(
                            message = data.serviceMessage.orEmpty(),
                            endTime = data.serviceEndTime.orEmpty()
                        )
                    }
                    // 維護結束且正卡在維護畫面 → 走完整啟動流程回到 App
                    _authState.value is AuthState.ServiceUnavailable -> checkToken()
                }
            } catch (e: Exception) {
                Log.e("DAE_Develop", "重查服務狀態錯誤", e)
            }
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object NeedSchoolSelection : AuthState()
    data class ServiceUnavailable(val message: String, val endTime: String) : AuthState()
    data class NeedAppUpdate(val currentVersion: String, val requiredVersion: String) : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}
