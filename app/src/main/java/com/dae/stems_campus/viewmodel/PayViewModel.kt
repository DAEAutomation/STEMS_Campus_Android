package com.dae.stems_campus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.PayModel
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.PayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PayViewModel @Inject constructor(private val payRepository: PayRepository) : ViewModel() {

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    //撥款
    private val _disbursementData = MutableStateFlow<PayModel.DisbursementData?>(null)
    val disbursementData: StateFlow<PayModel.DisbursementData?> = _disbursementData

    private val _resDisbursementSuccessFlag = MutableStateFlow(false)
    val resDisbursementSuccessFlag: StateFlow<Boolean> get() = _resDisbursementSuccessFlag

    private val _showDisbursementFailDialogFlag = MutableStateFlow(false)
    val showDisbursementFailDialogFlag: StateFlow<Boolean> get() = _showDisbursementFailDialogFlag

    private val _showDisbursementFailMsg = MutableStateFlow<String?>("")
    val showDisbursementFailMsg: StateFlow<String?> = _showDisbursementFailMsg

    private val _showDisbursementInputFailFlag = MutableStateFlow(false)
    val showDisbursementInputFailFlag: StateFlow<Boolean> get() = _showDisbursementInputFailFlag

    private val _showDisbursementInputFailMsg = MutableStateFlow<String?>("")
    val showDisbursementInputFailMsg: StateFlow<String?> = _showDisbursementInputFailMsg

    //撥款至宿舍錢包
    fun disbursementAction(aWalletId: Int, aAmount: Int) {
        viewModelScope.launch {
            if (aWalletId <= 0) {
                _showDisbursementFailDialogFlag.value = true
                _showDisbursementFailMsg.value = "DormitoryNotBound"
                return@launch
            }
            if (aAmount <= 0) {
                _showDisbursementInputFailFlag.value = true
                _showDisbursementInputFailMsg.value = "AmountInvalid"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = payRepository.disbursement(aWalletId, aAmount)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _disbursementData.value = result.data
                    _resDisbursementSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "disbursement Res ->${result.message}")
                    _showLoadingView.value = false
                    _showDisbursementFailDialogFlag.value = true
                    _showDisbursementFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showDisbursementFailDialogFlag.value = true
                    _showDisbursementFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetResDisbursementSuccessFlag(value: Boolean) {
        _resDisbursementSuccessFlag.value = value
    }

    fun resetShowDisbursementFailDialogFlag(value: Boolean) {
        _showDisbursementFailDialogFlag.value = value
    }

    fun resetShowDisbursementInputFailFlag(value: Boolean) {
        _showDisbursementInputFailFlag.value = value
    }

    fun resetDisbursementData() {
        _disbursementData.value = null
    }
}
