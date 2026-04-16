package com.dae.stems_campus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.LoginRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private var loginRepository: LoginRepository, private val userPreferences: UserPreferencesRepository, private val credentialRepository: CredentialRepository) : ViewModel() {

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    private val _userNameText = MutableStateFlow("")
    val userNameText: StateFlow<String> = _userNameText

    private val _passwordText = MutableStateFlow("")
    val passwordText: StateFlow<String> = _passwordText

    private val _resLoginSuccessFlag = MutableStateFlow(false)
    val resLoginSuccessFlag: StateFlow<Boolean> get() = _resLoginSuccessFlag

    private val _showLoginFailMsgDialogFlag = MutableStateFlow(false)
    val showLoginFailMsgDialogFlag: StateFlow<Boolean> get() = _showLoginFailMsgDialogFlag

    private val _showLoginFailMsg = MutableStateFlow<String?>("")
    val showLoginFailMsg: StateFlow<String?> = _showLoginFailMsg

    private val _rememberLoginInfoChecked = MutableStateFlow(false)
    val rememberLoginInfoChecked: StateFlow<Boolean> get() = _rememberLoginInfoChecked

    private val _UUID = MutableStateFlow("")
    val UUID: StateFlow<String> = _UUID

    //密碼驗證
    private val _resVerifyPasswordSuccessFlag = MutableStateFlow(false)
    val resVerifyPasswordSuccessFlag: StateFlow<Boolean> get() = _resVerifyPasswordSuccessFlag

    private val _showVerifyPasswordFailDialogFlag = MutableStateFlow(false)
    val showVerifyPasswordFailDialogFlag: StateFlow<Boolean> get() = _showVerifyPasswordFailDialogFlag

    private val _showVerifyPasswordFailMsg = MutableStateFlow<String?>("")
    val showVerifyPasswordFailMsg: StateFlow<String?> = _showVerifyPasswordFailMsg

    //登出
    private val _resLogoutSuccessFlag = MutableStateFlow(false)
    val resLogoutSuccessFlag: StateFlow<Boolean> get() = _resLogoutSuccessFlag

    private val _showLogoutFailDialogFlag = MutableStateFlow(false)
    val showLogoutFailDialogFlag: StateFlow<Boolean> get() = _showLogoutFailDialogFlag

    private val _showLogoutFailMsg = MutableStateFlow<String?>("")
    val showLogoutFailMsg: StateFlow<String?> = _showLogoutFailMsg


    init {
        loadLoginPreferences()
    }

    private fun loadLoginPreferences() {
        viewModelScope.launch {
            coroutineScope {
                userPreferences.getRememberLoginInfoCheckedFlow.firstOrNull()?.let { isChecked ->
                    if (isChecked) {
                        _userNameText.value = credentialRepository.getUsername() ?: ""
                        _passwordText.value = credentialRepository.getPassword() ?: ""
                    }
                }
//                _email.value = credentialRepository.getUsername() ?: ""
//                _rToken.value = credentialRepository.getRefreshToken() ?: ""
            }
            launch {
                userPreferences.getRememberLoginInfoCheckedFlow.collectLatest { isChecked ->
                    _rememberLoginInfoChecked.value = isChecked
                }

                userPreferences.getUUIDValue.collectLatest { value ->
                    _UUID.value = value
                }
            }
        }
    }

    //登入
    fun loginAction(userName: String, password: String, uuid: String) {
        viewModelScope.launch {
            if (uuid.isEmpty()) {
                val uuidNew = java.util.UUID.randomUUID().toString()
                _UUID.value = uuidNew
            }
            if (userName.isEmpty()) {
                _showLoginFailMsgDialogFlag.value = true
                _showLoginFailMsg.value = "AccountNotEntered"
            }else if (password.isEmpty()){
                _showLoginFailMsgDialogFlag.value = true
                _showLoginFailMsg.value = "PasswordNotEntered"
            }else{
                Log.d("DAE_Develop", "UUID->${_UUID.value}")
                _showLoadingView.value = true
                when (val result = loginRepository.getLoginData(userName,password,_UUID.value)) {
                    is BaseRepository.Result.Success -> {
                        _showLoadingView.value = false
                        // 儲存帳號密碼至Credential
                        credentialRepository.saveCredentials(userName,password)

                        // 儲存Mqtt帳號密碼至Credential
                        val mqttUsername = result.data?.mqtt?.username ?: ""
                        val mqttPw = result.data?.mqtt?.password ?: ""
                        credentialRepository.saveMqttCredentials(mqttUsername,mqttPw)

                        // 儲存token至Credential
                        val accessToken = result.data?.accessToken ?: ""
                        val refreshToken = result.data?.refreshToken ?: ""
                        credentialRepository.saveTokenCredentials(accessToken,refreshToken)
                        result.data?.user?.name?.let {
                            saveNameValueToDataStore(it)
                        }
                        result.data?.mqtt?.topicPrefix?.let {
                            saveMqttTopicValueToDataStore(it)
                        }
                        result.data?.mqtt?.brokerInternal?.let {
                            saveMqttHostValueToDataStore(it)
                        }
                        result.data?.mqtt?.portInternal?.let {
                            saveMqttPortValueToDataStore(it)
                        }

                        // 儲存 LoginInfo Checked
                        saveRememberLoginInfoCheckedValueToDataStore(rememberLoginInfoChecked.value)
                        // 儲存 UUID
                        saveUUIDValueToDataStore(_UUID.value)

                        _resLoginSuccessFlag.value = true
                    }
                    is BaseRepository.Result.Error -> {
                        _showLoadingView.value = false
                        _showLoginFailMsgDialogFlag.value = true
                        _showLoginFailMsg.value = result.message
                    }
                    is BaseRepository.Result.Unauthorized -> {
                        _showLoadingView.value = false
                    }
                }
            }
        }
    }

    // 密碼驗證
    fun verifyPasswordAction(aPassword: String, aType: String, aDeviceID: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = loginRepository.verifyPassword(aPassword,aType,aDeviceID)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resVerifyPasswordSuccessFlag.value = true
                    result.data
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "verifyPassword Res ->${result.message}")
                    _showLoadingView.value = false
                    _showVerifyPasswordFailDialogFlag.value = true
                    _showVerifyPasswordFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showVerifyPasswordFailDialogFlag.value = true
                    _showVerifyPasswordFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun logoutAction() {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = loginRepository.logout()) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resLogoutSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showLogoutFailDialogFlag.value = true
                    _showLogoutFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showLogoutFailDialogFlag.value = true
                    _showLogoutFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }



    // 儲存 Name(使用者名稱)
    private fun saveNameValueToDataStore(value: String) {
        viewModelScope.launch {
            userPreferences.setNameValue(value)
        }
    }

    // 儲存 UUID
    private fun saveUUIDValueToDataStore(value: String) {
        viewModelScope.launch {
            userPreferences.setUUIDValue(value)
        }
    }

    // 儲存 mqtt topic
    private fun saveMqttTopicValueToDataStore(value: String) {
        viewModelScope.launch {
            userPreferences.setMqttTopicValue(value)
        }
    }

    // 儲存 mqtt host
    private fun saveMqttHostValueToDataStore(value: String) {
        viewModelScope.launch {
            userPreferences.setMqttHostValue(value)
        }
    }

    // 儲存 mqtt port
    private fun saveMqttPortValueToDataStore(value: Int) {
        viewModelScope.launch {
            userPreferences.setMqttPortValue(value)
        }
    }

    // 儲存 LoginInfo Checked
    private fun saveRememberLoginInfoCheckedValueToDataStore(value: Boolean) {
        viewModelScope.launch {
            userPreferences.setRememberLoginInfoChecked(value)
        }
    }

    fun updateInputAccount(value: String) {
        _userNameText.value = value
    }

    fun updateInputPassword(value: String) {
        _passwordText.value = value
    }

    fun updateRememberLoginInfoChecked(value: Boolean) {
        _rememberLoginInfoChecked.value = value
    }

    fun resetLoginSuccessFlag(value: Boolean) {
        _resLoginSuccessFlag.value = value
    }

    fun resetShowLoginFailMsgDialogFlag(value: Boolean) {
        _showLoginFailMsgDialogFlag.value = value
    }

    fun resetResVerifyPasswordSuccessFlag(value: Boolean) {
        _resVerifyPasswordSuccessFlag.value = value
    }

    fun resetShowVerifyPasswordFailDialogFlag(value: Boolean) {
        _showVerifyPasswordFailDialogFlag.value = value
    }

    fun resetResLogoutSuccessFlag(value: Boolean) {
        _resLogoutSuccessFlag.value = value
    }

    fun resetShowLogoutFailDialogFlag(value: Boolean) {
        _showLogoutFailDialogFlag.value = value
    }
}