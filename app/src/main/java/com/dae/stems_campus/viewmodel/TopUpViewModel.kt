package com.dae.stems_campus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.TopUpModel
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.TopUpRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopUpViewModel @Inject constructor(private var topUpRepository: TopUpRepository, private val userPreferences: UserPreferencesRepository, private val credentialRepository: CredentialRepository) : ViewModel() {

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    //掃描儲值機
    private val _resScanDepositSuccessFlag = MutableStateFlow(false)
    val resScanDepositSuccessFlag: StateFlow<Boolean> get() = _resScanDepositSuccessFlag

    private val _showScanDepositFailDialogFlag = MutableStateFlow(false)
    val showScanDepositFailDialogFlag: StateFlow<Boolean> get() = _showScanDepositFailDialogFlag

    private val _showScanDepositFailMsg = MutableStateFlow<String?>("")
    val showScanDepositFailMsg: StateFlow<String?> = _showScanDepositFailMsg


    //發起儲值
    private val _resStartTopUpSuccessFlag = MutableStateFlow(false)
    val resStartTopUpSuccessFlag: StateFlow<Boolean> get() = _resStartTopUpSuccessFlag

    private val _startTopUpData = MutableStateFlow<TopUpModel.StartTopUpData?>(null)
    val startTopUpData: StateFlow<TopUpModel.StartTopUpData?> = _startTopUpData

    private val _showStartTopUpFailDialogFlag = MutableStateFlow(false)
    val showStartTopUpFailDialogFlag: StateFlow<Boolean> get() = _showStartTopUpFailDialogFlag

    private val _showStartTopUpFailMsg = MutableStateFlow<String?>("")
    val showStartTopUpFailMsg: StateFlow<String?> = _showStartTopUpFailMsg

    private val _UUID = MutableStateFlow("")
    val UUID: StateFlow<String> = _UUID

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

    fun scanDepositAction(depositCode: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = topUpRepository.scanDeposit(depositCode)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resScanDepositSuccessFlag.value = true

                    val hostUUID = result.data.mqtt?.hostUuid ?: ""
                    val depositMac = result.data.mqtt?.depositMac ?: ""
                    val subscriptionTopic = "${hostUUID}/${depositMac}/${_UUID.value}/server/request/#"
                    saveSubscriptionTopicToDataStore(subscriptionTopic)
                    saveDepositMacAddressToDataStore(depositMac)
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showScanDepositFailDialogFlag.value = true
                    _showScanDepositFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showScanDepositFailDialogFlag.value = true
                    _showScanDepositFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }


    fun startTopUpAction(aID: String, depositCode: String, deviceID: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = topUpRepository.startTopUp(depositCode,aID, deviceID)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resStartTopUpSuccessFlag.value = true
                    _startTopUpData.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showStartTopUpFailDialogFlag.value = true
                    _showStartTopUpFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showStartTopUpFailDialogFlag.value = true
                    _showStartTopUpFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }



    // 儲存 Subscription Topic  Deposit MacAddress
    private fun saveSubscriptionTopicToDataStore(value: String) {
        viewModelScope.launch {
            userPreferences.setSubscriptionTopiValue(value)
        }
    }

    // 儲存 Deposit MacAddress
    private fun saveDepositMacAddressToDataStore(value: String) {
        viewModelScope.launch {
            userPreferences.setDepositMacAddressValue(value)
        }
    }


    fun resetScanDepositSuccessFlag(value: Boolean) {
        _resScanDepositSuccessFlag.value = value
    }

    fun resetScanDepositFailDialogFlag(value: Boolean) {
        _showScanDepositFailDialogFlag.value = value
    }

    fun resetStartTopUpSuccessFlag(value: Boolean) {
        _resStartTopUpSuccessFlag.value = value
    }

    fun resetStartTopUpFailDialogFlag(value: Boolean) {
        _showStartTopUpFailDialogFlag.value = value
    }


}