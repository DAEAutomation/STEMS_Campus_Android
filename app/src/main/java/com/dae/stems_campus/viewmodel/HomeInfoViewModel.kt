package com.dae.stems_campus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.ProfileRepository
import com.dae.stems_campus.data.repository.ScanRepository
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
class HomeInfoViewModel @Inject constructor(private var scanRepository: ScanRepository, private var profileRepository: ProfileRepository, private val userPreferences: UserPreferencesRepository, private val credentialRepository: CredentialRepository) : ViewModel() {

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    private val _resScanInfoSuccessFlag = MutableStateFlow(false)
    val resScanInfoSuccessFlag: StateFlow<Boolean> get() = _resScanInfoSuccessFlag

    private val _scanInfo = MutableStateFlow<ScanModel.ScanData?>(null)
    val scanInfo: StateFlow<ScanModel.ScanData?> = _scanInfo

    private val _showScanInfoFailDialogFlag = MutableStateFlow(false)
    val showScanInfoFailDialogFlag: StateFlow<Boolean> get() = _showScanInfoFailDialogFlag

    private val _showScanInfoFailMsg = MutableStateFlow<String?>("")
    val showScanInfoFailMsg: StateFlow<String?> = _showScanInfoFailMsg

    private val _UUID = MutableStateFlow("")
    val UUID: StateFlow<String> = _UUID

    // 供電
    private val _resStartPowerSuccessFlag = MutableStateFlow(false)
    val resStartPowerSuccessFlag: StateFlow<Boolean> get() = _resStartPowerSuccessFlag

    private val _showStartPowerFailDialogFlag = MutableStateFlow(false)
    val showStartPowerFailDialogFlag: StateFlow<Boolean> get() = _showStartPowerFailDialogFlag

    private val _showStartPowerFailMsg = MutableStateFlow<String?>("")
    val showStartPowerFailMsg: StateFlow<String?> = _showStartPowerFailMsg

    private val _showLoadingViewByStartPowerStudent = MutableStateFlow(false)
    val showLoadingViewByStartPowerStudent: StateFlow<Boolean> get() = _showLoadingViewByStartPowerStudent

    //停止用電
    private val _resStopPowerSuccessFlag = MutableStateFlow(false)
    val resStopPowerSuccessFlag: StateFlow<Boolean> get() = _resStopPowerSuccessFlag

    private val _stopPowerData = MutableStateFlow<ScanModel.StopPowerData?>(null)
    val stopPowerData: StateFlow<ScanModel.StopPowerData?> = _stopPowerData

    private val _showStopPowerFailDialogFlag = MutableStateFlow(false)
    val showStopPowerFailDialogFlag: StateFlow<Boolean> get() = _showStopPowerFailDialogFlag

    private val _showStopPowerFailMsg = MutableStateFlow<String?>("")
    val showStopPowerFailMsg: StateFlow<String?> = _showStopPowerFailMsg


    //使用者裝置資訊
    private val _resUsingDeviceDetailSuccessFlag = MutableStateFlow(false)
    val resUsingDeviceDetailSuccessFlag: StateFlow<Boolean> get() = _resUsingDeviceDetailSuccessFlag

    private val _usingDeviceDetail = MutableStateFlow<ProfileModel.ActiveSession?>(null)
    val usingDeviceDetail: StateFlow<ProfileModel.ActiveSession?> = _usingDeviceDetail

    private val _showUsingDeviceDetailFailDialogFlag = MutableStateFlow(false)
    val showUsingDeviceDetailFailDialogFlag: StateFlow<Boolean> get() = _showUsingDeviceDetailFailDialogFlag

    private val _showUsingDeviceDetailFailMsg = MutableStateFlow<String?>("")
    val showUsingDeviceDetailFailMsg: StateFlow<String?> = _showUsingDeviceDetailFailMsg

    //冷氣控制
    private val _resControlAcSuccessFlag = MutableStateFlow(false)
    val resControlAcSuccessFlag: StateFlow<Boolean> get() = _resControlAcSuccessFlag

    private val _showControlAcFailDialogFlag = MutableStateFlow(false)
    val showControlAcFailDialogFlag: StateFlow<Boolean> get() = _showControlAcFailDialogFlag

    private val _showControlAcFailMsg = MutableStateFlow<String?>("")
    val showControlAcFailMsg: StateFlow<String?> = _showControlAcFailMsg

    private val _controlAcCommand = MutableStateFlow<String?>("")
    val controlAcCommand: StateFlow<String?> = _controlAcCommand

    private val _showLoadingViewBAcControlStudent = MutableStateFlow(false)
    val showLoadingViewBAcControlStudent: StateFlow<Boolean> get() = _showLoadingViewBAcControlStudent

    init {
        loadLoginPreferences()
    }

    fun loadLoginPreferences() {
        viewModelScope.launch {
            launch {
                userPreferences.getUUIDValue.collectLatest { value ->
                    _UUID.value = value
                }
            }
        }
    }

    // 掃描取得資訊
    fun getScanInfoAction(aQrCode: String, aDeviceID: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = scanRepository.scanInfo(qrCode = aQrCode, deviceID = aDeviceID)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resScanInfoSuccessFlag.value = true
                    _scanInfo.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showScanInfoFailDialogFlag.value = true
                    _showScanInfoFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showScanInfoFailDialogFlag.value = true
                    _showScanInfoFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 啟用用電
    fun startPowerAction(deviceCode: String, deviceID: String, sessionID: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = scanRepository.startPower(deviceCode,deviceID,sessionID)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resStartPowerSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showStartPowerFailDialogFlag.value = true
                    _showStartPowerFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showStartPowerFailDialogFlag.value = true
                    _showStartPowerFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 啟用用電(學生)
    fun startPowerByStudentAction(deviceCode: String, deviceID: String, sessionID: String, acValue: Boolean) {
        viewModelScope.launch {
            _showLoadingViewByStartPowerStudent.value = true
            when (val result = scanRepository.startPowerByStudent(deviceCode,deviceID,sessionID, acValue)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingViewByStartPowerStudent.value = false
                    _resStartPowerSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingViewByStartPowerStudent.value = false
                    _showStartPowerFailDialogFlag.value = true
                    _showStartPowerFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingViewByStartPowerStudent.value = false
                    _showStartPowerFailDialogFlag.value = true
                    _showStartPowerFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 停止用電
    fun stopPowerAction(deviceCode: String, deviceID: String, controlToken: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = scanRepository.stopPower(deviceCode,deviceID, controlToken)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resStopPowerSuccessFlag.value = true
                    _stopPowerData.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showStopPowerFailDialogFlag.value = true
                    _showStopPowerFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showStopPowerFailDialogFlag.value = true
                    _showStopPowerFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 使用中裝置內容
    fun getUsingDeviceDetailAction(deviceCode: String, deviceID: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = profileRepository.getUsingDeviceDetail(deviceCode, deviceID)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resUsingDeviceDetailSuccessFlag.value = true
                    _usingDeviceDetail.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showUsingDeviceDetailFailDialogFlag.value = true
                    _showUsingDeviceDetailFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showUsingDeviceDetailFailDialogFlag.value = true
                    _showUsingDeviceDetailFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 冷氣控制
    fun controlAcAction(deviceID: String, controlToken: String, command: String) {
        viewModelScope.launch {
            _showLoadingViewBAcControlStudent.value = true
            _controlAcCommand.value = command
            when (val result = scanRepository.control(deviceID,controlToken,command)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingViewBAcControlStudent.value = false
                    _resControlAcSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingViewBAcControlStudent.value = false
                    _showControlAcFailDialogFlag.value = true
                    _showControlAcFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingViewBAcControlStudent.value = false
                    _showControlAcFailDialogFlag.value = true
                    _showControlAcFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }


    fun resetScanInfoSuccessFlag(value: Boolean) {
        _resScanInfoSuccessFlag.value = value
    }

    fun resetScanInfoFailDialogFlag(value: Boolean) {
        _showScanInfoFailDialogFlag.value = value
    }

    fun resetStartPowerSuccessFlag(value: Boolean) {
        _resStartPowerSuccessFlag.value = value
    }

    fun resetStartPowerFailDialogFlag(value: Boolean) {
        _showStartPowerFailDialogFlag.value = value
    }

    fun resetUsingDeviceDetailSuccessFlag(value: Boolean) {
        _resUsingDeviceDetailSuccessFlag.value = value
    }

    fun resetUsingDeviceDetailFailDialogFlag(value: Boolean) {
        _showUsingDeviceDetailFailDialogFlag.value = value
    }

    fun resetStopPowerSuccessFlag(value: Boolean) {
        _resStopPowerSuccessFlag.value = value
    }

    fun resetStopPowerFailDialogFlag(value: Boolean) {
        _showStopPowerFailDialogFlag.value = value
    }

    fun resetControlAcSuccessFlag(value: Boolean) {
        _resControlAcSuccessFlag.value = value
    }

    fun resetControlAcFailDialogFlag(value: Boolean) {
        _showControlAcFailDialogFlag.value = value
    }
}