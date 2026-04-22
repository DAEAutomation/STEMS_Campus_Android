package com.dae.stems_campus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import com.dae.stems_campus.network.BaseUrlHolder
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val baseUrlHolder: BaseUrlHolder
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    /**
     * 檢查 baseUrl 是否設定 + Token 狀態
     */
    fun checkToken() {
        viewModelScope.launch {
            try {
                // 先檢查是否已選擇學校（baseUrl 是否存在）
                val storedBaseUrl = userPreferences.getApiDomainValue.firstOrNull().orEmpty()
                if (storedBaseUrl.isEmpty()) {
                    _authState.value = AuthState.NeedSchoolSelection
                    return@launch
                }
                // 把 baseUrl 灌進 holder，後續 API 請求才會用對位置
                baseUrlHolder.baseUrl = storedBaseUrl

                val isValid = tokenManager.isTokenValid()
                _authState.value = if (isValid) {
                    AuthState.Authenticated
                } else {
                    AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                Log.e("DAE_Develop", "檢查 Token 錯誤", e)
                _authState.value = AuthState.Unauthenticated
            }

        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object NeedSchoolSelection : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}