package com.dae.stems_campus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.SettingModel
import com.dae.stems_campus.data.repository.AccountRepository
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.ProfileRepository
import com.dae.stems_campus.data.repository.SettingRepository
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
class SettingViewModel @Inject constructor(private var profileRepository: ProfileRepository, private val userPreferences: UserPreferencesRepository, private val credentialRepository: CredentialRepository, private val accountRepository: AccountRepository, private val settingRepository: SettingRepository) : ViewModel() {


    private val _UUID = MutableStateFlow("")
    val UUID: StateFlow<String> = _UUID

    //生物辨識
    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled

    //變更密碼
    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> = _showLoadingView

    private val _oldPasswordText = MutableStateFlow("")
    val oldPasswordText: StateFlow<String> = _oldPasswordText

    private val _newPasswordText = MutableStateFlow("")
    val newPasswordText: StateFlow<String> = _newPasswordText

    private val _confirmNewPasswordText = MutableStateFlow("")
    val confirmNewPasswordText: StateFlow<String> = _confirmNewPasswordText

    private val _resChangePasswordSuccessFlag = MutableStateFlow(false)
    val resChangePasswordSuccessFlag: StateFlow<Boolean> = _resChangePasswordSuccessFlag

    private val _showChangePasswordFailDialogFlag = MutableStateFlow(false)
    val showChangePasswordFailDialogFlag: StateFlow<Boolean> = _showChangePasswordFailDialogFlag

    private val _showChangePasswordFailMsg = MutableStateFlow<String?>("")
    val showChangePasswordFailMsg: StateFlow<String?> = _showChangePasswordFailMsg

    // 1=舊密碼, 2=新密碼格式, 3=確認新密碼不一致
    private val _showChangePasswordInputFailTag = MutableStateFlow(0)
    val showChangePasswordInputFailTag: StateFlow<Int> = _showChangePasswordInputFailTag

    private val _showChangePasswordInputFailMsg = MutableStateFlow<String?>("")
    val showChangePasswordInputFailMsg: StateFlow<String?> = _showChangePasswordInputFailMsg

    //實體卡綁定
    private val _uidText = MutableStateFlow("")
    val uidText: StateFlow<String> = _uidText

    private val _resCardBindingSuccessFlag = MutableStateFlow(false)
    val resCardBindingSuccessFlag: StateFlow<Boolean> = _resCardBindingSuccessFlag

    private val _showCardBindingFailDialogFlag = MutableStateFlow(false)
    val showCardBindingFailDialogFlag: StateFlow<Boolean> = _showCardBindingFailDialogFlag

    private val _showCardBindingFailMsg = MutableStateFlow<String?>("")
    val showCardBindingFailMsg: StateFlow<String?> = _showCardBindingFailMsg

    private val _showCardBindingInputFailFlag = MutableStateFlow(false)
    val showCardBindingInputFailFlag: StateFlow<Boolean> = _showCardBindingInputFailFlag

    private val _showCardBindingInputFailMsg = MutableStateFlow<String?>("")
    val showCardBindingInputFailMsg: StateFlow<String?> = _showCardBindingInputFailMsg

    //實體卡解除綁定
    private val _resCardUnBindingSuccessFlag = MutableStateFlow(false)
    val resCardUnBindingSuccessFlag: StateFlow<Boolean> = _resCardUnBindingSuccessFlag

    private val _showCardUnBindingFailDialogFlag = MutableStateFlow(false)
    val showCardUnBindingFailDialogFlag: StateFlow<Boolean> = _showCardUnBindingFailDialogFlag

    private val _showCardUnBindingFailMsg = MutableStateFlow<String?>("")
    val showCardUnBindingFailMsg: StateFlow<String?> = _showCardUnBindingFailMsg

    //宿舍綁定掃描取得資訊
    private val _dormScanInfoData = MutableStateFlow<SettingModel.DormScanInfoData?>(null)
    val dormScanInfoData: StateFlow<SettingModel.DormScanInfoData?> = _dormScanInfoData

    private val _resDormScanInfoSuccessFlag = MutableStateFlow(false)
    val resDormScanInfoSuccessFlag: StateFlow<Boolean> = _resDormScanInfoSuccessFlag

    private val _showDormScanInfoFailDialogFlag = MutableStateFlow(false)
    val showDormScanInfoFailDialogFlag: StateFlow<Boolean> = _showDormScanInfoFailDialogFlag

    private val _showDormScanInfoFailMsg = MutableStateFlow<String?>("")
    val showDormScanInfoFailMsg: StateFlow<String?> = _showDormScanInfoFailMsg

    //宿舍綁定
    private val _dormBindingData = MutableStateFlow<SettingModel.DormBindingData?>(null)
    val dormBindingData: StateFlow<SettingModel.DormBindingData?> = _dormBindingData

    private val _resDormBindingSuccessFlag = MutableStateFlow(false)
    val resDormBindingSuccessFlag: StateFlow<Boolean> = _resDormBindingSuccessFlag

    private val _showDormBindingFailDialogFlag = MutableStateFlow(false)
    val showDormBindingFailDialogFlag: StateFlow<Boolean> = _showDormBindingFailDialogFlag

    private val _showDormBindingFailMsg = MutableStateFlow<String?>("")
    val showDormBindingFailMsg: StateFlow<String?> = _showDormBindingFailMsg

    //宿舍解除綁定
    private val _resDormUnBindingSuccessFlag = MutableStateFlow(false)
    val resDormUnBindingSuccessFlag: StateFlow<Boolean> = _resDormUnBindingSuccessFlag

    private val _showDormUnBindingFailDialogFlag = MutableStateFlow(false)
    val showDormUnBindingFailDialogFlag: StateFlow<Boolean> = _showDormUnBindingFailDialogFlag

    private val _showDormUnBindingFailMsg = MutableStateFlow<String?>("")
    val showDormUnBindingFailMsg: StateFlow<String?> = _showDormUnBindingFailMsg

    //目前綁定的宿舍
    private val _myDormitoryData = MutableStateFlow<SettingModel.MyDormitoryData?>(null)
    val myDormitoryData: StateFlow<SettingModel.MyDormitoryData?> = _myDormitoryData

    private val _showMyDormitoryFailDialogFlag = MutableStateFlow(false)
    val showMyDormitoryFailDialogFlag: StateFlow<Boolean> = _showMyDormitoryFailDialogFlag

    private val _showMyDormitoryFailMsg = MutableStateFlow<String?>("")
    val showMyDormitoryFailMsg: StateFlow<String?> = _showMyDormitoryFailMsg

    init {
        loadLoginPreferences()
    }

    private fun loadLoginPreferences() {
        viewModelScope.launch {
            launch {
                userPreferences.getUUIDValue.collectLatest { value ->
                    _UUID.value = value
                }
            }
        }
    }


    fun getBiometricValue() {
        viewModelScope.launch {
            coroutineScope {
                userPreferences.getBiometricValue.firstOrNull()?.let { value ->
                    _isBiometricEnabled.value = value
                }
            }
        }
    }

    //紀錄生物辦辨識是否啟動
    fun updateBiometricValue(value: Boolean) {
        _isBiometricEnabled.value = value
        viewModelScope.launch {
            userPreferences.setBiometricValue(value)
        }
    }

    fun updateInputOldPassword(value: String) {
        _oldPasswordText.value = value
    }

    fun updateInputNewPassword(value: String) {
        _newPasswordText.value = value
    }

    fun updateInputConfirmNewPassword(value: String) {
        _confirmNewPasswordText.value = value
    }

    //變更密碼
    fun changePasswordAction(aOldPassword: String, aNewPassword: String, aConfirmNewPassword: String) {
        viewModelScope.launch {
            if (aOldPassword.isEmpty()) {
                _showChangePasswordInputFailTag.value = 1
                _showChangePasswordInputFailMsg.value = "OldPasswordNotEntered"
                return@launch
            }
            if (aNewPassword.isEmpty()) {
                _showChangePasswordInputFailTag.value = 2
                _showChangePasswordInputFailMsg.value = "NewPasswordNotEntered"
                return@launch
            }
            // 8~20 位數，需同時含數字與英文字母
            if (!aNewPassword.matches(Regex("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,20}$"))) {
                _showChangePasswordInputFailTag.value = 2
                _showChangePasswordInputFailMsg.value = "NewPasswordInvalidFormat"
                return@launch
            }
            if (aNewPassword != aConfirmNewPassword) {
                _showChangePasswordInputFailTag.value = 3
                _showChangePasswordInputFailMsg.value = "PasswordMismatch"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = accountRepository.changePassword(aOldPassword, aNewPassword, aConfirmNewPassword)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resChangePasswordSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "changePassword Res ->${result.message}")
                    _showLoadingView.value = false
                    _showChangePasswordFailDialogFlag.value = true
                    _showChangePasswordFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showChangePasswordFailDialogFlag.value = true
                    _showChangePasswordFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetResChangePasswordSuccessFlag(value: Boolean) {
        _resChangePasswordSuccessFlag.value = value
    }

    fun resetShowChangePasswordFailDialogFlag(value: Boolean) {
        _showChangePasswordFailDialogFlag.value = value
    }

    fun resetShowChangePasswordInputFailTag(value: Int) {
        _showChangePasswordInputFailTag.value = value
    }

    fun resetChangePasswordInputs() {
        _oldPasswordText.value = ""
        _newPasswordText.value = ""
        _confirmNewPasswordText.value = ""
        _showChangePasswordInputFailTag.value = 0
    }

    fun updateInputUid(value: String) {
        _uidText.value = value
    }

    //實體卡綁定
    fun cardBindingAction(aUid: String) {
        viewModelScope.launch {
            if (aUid.isEmpty()) {
                _showCardBindingInputFailFlag.value = true
                _showCardBindingInputFailMsg.value = "UIDNotEntered"
                return@launch
            }
            // 卡號需 8 碼 16 進位 (0-9, A-F)
            if (!aUid.matches(Regex("^[0-9A-F]{8}$"))) {
                _showCardBindingInputFailFlag.value = true
                _showCardBindingInputFailMsg.value = "UIDInvalidFormat"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = settingRepository.cardBinding(aUid)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resCardBindingSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "cardBinding Res ->${result.message}")
                    _showLoadingView.value = false
                    _showCardBindingFailDialogFlag.value = true
                    _showCardBindingFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showCardBindingFailDialogFlag.value = true
                    _showCardBindingFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetResCardBindingSuccessFlag(value: Boolean) {
        _resCardBindingSuccessFlag.value = value
    }

    fun resetShowCardBindingFailDialogFlag(value: Boolean) {
        _showCardBindingFailDialogFlag.value = value
    }

    fun resetShowCardBindingInputFailFlag(value: Boolean) {
        _showCardBindingInputFailFlag.value = value
    }

    fun resetCardBindingInputs() {
        _uidText.value = ""
        _showCardBindingInputFailFlag.value = false
    }

    //實體卡解除綁定 (帶空字串給後端表示解綁)
    fun cardUnBindingAction() {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = settingRepository.cardBinding("")) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resCardUnBindingSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "cardUnBinding Res ->${result.message}")
                    _showLoadingView.value = false
                    _showCardUnBindingFailDialogFlag.value = true
                    _showCardUnBindingFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showCardUnBindingFailDialogFlag.value = true
                    _showCardUnBindingFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetResCardUnBindingSuccessFlag(value: Boolean) {
        _resCardUnBindingSuccessFlag.value = value
    }

    fun resetShowCardUnBindingFailDialogFlag(value: Boolean) {
        _showCardUnBindingFailDialogFlag.value = value
    }

    // Dormitory Scan-宿舍綁定掃描取得資訊

    fun dormScanInfoAction(qrCode: String) {
        viewModelScope.launch {
            if (qrCode.isEmpty()) {
                _showDormScanInfoFailDialogFlag.value = true
                _showDormScanInfoFailMsg.value = "QRCodeInvalid"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = settingRepository.dormScanInfo(qrCode)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resDormScanInfoSuccessFlag.value = true
                    _dormScanInfoData.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "dormScanInfo Res ->${result.message}")
                    _showLoadingView.value = false
                    _showDormScanInfoFailDialogFlag.value = true
                    _showDormScanInfoFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showDormScanInfoFailDialogFlag.value = true
                    _showDormScanInfoFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetShowDormScanInfoFailDialogFlag(value: Boolean) {
        _showDormScanInfoFailDialogFlag.value = value
    }

    fun resetDormScanInfoData() {
        _dormScanInfoData.value = null
    }

    fun resetResDormScanInfoSuccessFlag(value: Boolean) {
        _resDormScanInfoSuccessFlag.value = value
    }

    // Dormitory Binding-宿舍綁定

    fun dormBindingAction(qrCode: String) {
        viewModelScope.launch {
            if (qrCode.isEmpty()) {
                _showDormBindingFailDialogFlag.value = true
                _showDormBindingFailMsg.value = "QRCodeInvalid"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = settingRepository.dormBinding(qrCode)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _dormBindingData.value = result.data
                    _resDormBindingSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "dormBinding Res ->${result.message}")
                    _showLoadingView.value = false
                    _showDormBindingFailDialogFlag.value = true
                    _showDormBindingFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showDormBindingFailDialogFlag.value = true
                    _showDormBindingFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetResDormBindingSuccessFlag(value: Boolean) {
        _resDormBindingSuccessFlag.value = value
    }

    fun resetShowDormBindingFailDialogFlag(value: Boolean) {
        _showDormBindingFailDialogFlag.value = value
    }

    // Dormitory Unbinding-宿舍解除綁定

    fun dormUnBindingAction(aRoomID: Int) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = settingRepository.dormUnbinding(aRoomID)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resDormUnBindingSuccessFlag.value = true
                    //解綁後清掉本地宿舍資料
                    _myDormitoryData.value = null
                    _dormBindingData.value = null
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "dormUnBinding Res ->${result.message}")
                    _showLoadingView.value = false
                    _showDormUnBindingFailDialogFlag.value = true
                    _showDormUnBindingFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showDormUnBindingFailDialogFlag.value = true
                    _showDormUnBindingFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetResDormUnBindingSuccessFlag(value: Boolean) {
        _resDormUnBindingSuccessFlag.value = value
    }

    fun resetShowDormUnBindingFailDialogFlag(value: Boolean) {
        _showDormUnBindingFailDialogFlag.value = value
    }

    // 查詢目前綁哪房

    fun getMyDormitoryDataAction(aShowLoading: Boolean = true) {
        viewModelScope.launch {
            if (aShowLoading) _showLoadingView.value = true
            when (val result = settingRepository.getMyDormitoryData()) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _myDormitoryData.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "getMyDormitoryData Res ->${result.message}")
                    _showLoadingView.value = false
                    _myDormitoryData.value = null
                    _showMyDormitoryFailDialogFlag.value = true
                    _showMyDormitoryFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showMyDormitoryFailDialogFlag.value = true
                    _showMyDormitoryFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetShowMyDormitoryFailDialogFlag(value: Boolean) {
        _showMyDormitoryFailDialogFlag.value = value
    }
}