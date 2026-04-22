package com.dae.stems_campus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.RefundModel
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.RefundRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RefundViewModel @Inject constructor(private var refundRepository: RefundRepository, private val userPreferences: UserPreferencesRepository, private val credentialRepository: CredentialRepository) : ViewModel() {

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    // 申請退款
    private val _resRefundRequestSuccessFlag = MutableStateFlow(false)
    val resRefundRequestSuccessFlag: StateFlow<Boolean> get() = _resRefundRequestSuccessFlag

    private val _showRefundRequestFailDialogFlag = MutableStateFlow(false)
    val showRefundRequestFailDialogFlag: StateFlow<Boolean> get() = _showRefundRequestFailDialogFlag

    private val _showRefundRequestFailMsg = MutableStateFlow<String?>("")
    val showRefundRequestFailMsg: StateFlow<String?> = _showRefundRequestFailMsg

    private val _refundRequestData = MutableStateFlow<RefundModel.RefundRequestData?>(null)
    val refundRequestData: StateFlow<RefundModel.RefundRequestData?> = _refundRequestData

    // 進行退款-悠遊卡
    private val _resStartRefundSuccessFlag = MutableStateFlow(false)
    val resStartRefundSuccessFlag: StateFlow<Boolean> get() = _resStartRefundSuccessFlag

    private val _showStartRefundFailDialogFlag = MutableStateFlow(false)
    val showStartRefundFailDialogFlag: StateFlow<Boolean> get() = _showStartRefundFailDialogFlag

    private val _showStartRefundFailMsg = MutableStateFlow<String?>("")
    val showStartRefundFailMsg: StateFlow<String?> = _showStartRefundFailMsg

    private val _startRefundData = MutableStateFlow<RefundModel.StartRefundData?>(null)
    val startRefundData: StateFlow<RefundModel.StartRefundData?> = _startRefundData

    // 退款取消
    private val _resCancelRefundSuccessFlag = MutableStateFlow(false)
    val resCancelRefundSuccessFlag: StateFlow<Boolean> get() = _resCancelRefundSuccessFlag

    private val _showCancelRefundFailDialogFlag = MutableStateFlow(false)
    val showCancelRefundFailDialogFlag: StateFlow<Boolean> get() = _showCancelRefundFailDialogFlag

    private val _showCancelRefundFailMsg = MutableStateFlow<String?>("")
    val showCancelRefundFailMsg: StateFlow<String?> = _showCancelRefundFailMsg


    // 退款狀態查詢
    private val _resFetchRefundStatusSuccessFlag = MutableStateFlow(false)
    val resFetchRefundStatusSuccessFlag: StateFlow<Boolean> get() = _resFetchRefundStatusSuccessFlag

    private val _showFetchRefundStatusFailDialogFlag = MutableStateFlow(false)
    val showFetchRefundStatusFailDialogFlag: StateFlow<Boolean> get() = _showFetchRefundStatusFailDialogFlag

    private val _showFetchRefundStatusFailMsg = MutableStateFlow<String?>("")
    val showFetchRefundStatusFailMsg: StateFlow<String?> = _showFetchRefundStatusFailMsg

    private val _refundStatusData = MutableStateFlow<RefundModel.RefundStatusData?>(null)
    val refundStatusData: StateFlow<RefundModel.RefundStatusData?> = _refundStatusData


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

    // 申請退款
    fun refundRequestAction(refundType: Int) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = refundRepository.refundRequest(refundType)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resRefundRequestSuccessFlag.value = true
                    _refundRequestData.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showRefundRequestFailDialogFlag.value = true
                    _showRefundRequestFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showRefundRequestFailDialogFlag.value = true
                    _showRefundRequestFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 進行退款-悠遊卡
    fun startRefundAction(refundID: Int, depositCode: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = refundRepository.startRefund(refundID,UUID.value,depositCode)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resStartRefundSuccessFlag.value = true
                    _startRefundData.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showStartRefundFailDialogFlag.value = true
                    _showStartRefundFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showStartRefundFailDialogFlag.value = true
                    _showStartRefundFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 退款取消
    fun cancelRefundAction(refundID: Int) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = refundRepository.cancelRefund(refundID)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resCancelRefundSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showCancelRefundFailDialogFlag.value = true
                    _showCancelRefundFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showCancelRefundFailDialogFlag.value = true
                    _showCancelRefundFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 退款狀態查詢
    fun fetchRefundStatusAction() {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = refundRepository.fetchRefundStatus()) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resFetchRefundStatusSuccessFlag.value = true
                    _refundStatusData.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showFetchRefundStatusFailDialogFlag.value = true
                    _showFetchRefundStatusFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showFetchRefundStatusFailDialogFlag.value = true
                    _showFetchRefundStatusFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetResRefundRequestSuccessFailDialogFlag(value: Boolean) {
        _resRefundRequestSuccessFlag.value = value
    }

    fun resetShowRefundRequestFailDialogFlag(value: Boolean) {
        _showRefundRequestFailDialogFlag.value = value
    }

    fun resetResStartRefundSuccessFailDialogFlag(value: Boolean) {
        _resStartRefundSuccessFlag.value = value
    }

    fun resetShowStartRefundFailDialogFlag(value: Boolean) {
        _showStartRefundFailDialogFlag.value = value
    }

    fun resetFetchRefundStatusSuccessFailDialogFlag(value: Boolean) {
        _resFetchRefundStatusSuccessFlag.value = value
    }

    fun resetShowFetchRefundStatusFailDialogFlag(value: Boolean) {
        _showFetchRefundStatusFailDialogFlag.value = value
    }

    fun resetResCancelRefundSuccessFailDialogFlag(value: Boolean) {
        _resCancelRefundSuccessFlag.value = value
    }

    fun resetShowCancelRefundFailDialogFlag(value: Boolean) {
        _showCancelRefundFailDialogFlag.value = value
    }

}