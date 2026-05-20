package com.dae.stems_campus.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.MqttModel
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import com.dae.stems_campus.network.MqttHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MqttVIewModel @Inject constructor(application: Application, private val credentialRepository: CredentialRepository, private val userPreferences: UserPreferencesRepository) : AndroidViewModel(application) {

    private val mqttHelper = MqttHelper.getInstance(application)

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    // StateFlows for UI state
    val connectionState: StateFlow<MqttHelper.ConnectionState> = mqttHelper.connectionState
    val subscriptionState: StateFlow<MqttHelper.SubscriptionState> = mqttHelper.subscriptionState

    private var _isReceipt = MutableStateFlow(false)

    // Transaction Receipted
    private val _mqttTransactionReceiptedSuccessFlag = MutableStateFlow(false)
    val mqttTransactionReceiptedSuccessFlag: StateFlow<Boolean> get() = _mqttTransactionReceiptedSuccessFlag

    private val _mqttTransactionReceiptedFailFlag = MutableStateFlow(false)
    val mqttTransactionReceiptedFailFlag: StateFlow<Boolean> get() = _mqttTransactionReceiptedFailFlag

    private val _mqttTransactionReceiptedFailMsg = MutableStateFlow("")
    val mqttTransactionReceiptedFailMsg: StateFlow<String> get() = _mqttTransactionReceiptedFailMsg

    // Transaction Finish
    private val _mqttTransactionFinishSuccessFlag = MutableStateFlow(false)
    val mqttTransactionFinishSuccessFlag: StateFlow<Boolean> get() = _mqttTransactionFinishSuccessFlag

    private val _mqttTransactionFinishSuccessMsg = MutableStateFlow("")
    val mqttTransactionFinishSuccessMsg: StateFlow<String> get() = _mqttTransactionFinishSuccessMsg

    private val _mqttTransactionFinishFailFlag = MutableStateFlow(false)
    val mqttTransactionFinishFailFlag: StateFlow<Boolean> get() = _mqttTransactionFinishFailFlag

    private val _mqttTransactionFinishFailMsg = MutableStateFlow("")
    val mqttTransactionFinishFailMsg: StateFlow<String> get() = _mqttTransactionFinishFailMsg

    private val _transactionState = MutableStateFlow<MqttModel.TransactionState>(MqttModel.TransactionState.Idle)
    val transactionState: StateFlow<MqttModel.TransactionState> = _transactionState

    private var _creditAmount = MutableStateFlow(0)

    private val _finalCreditAmount = MutableStateFlow(0)
    val finalCreditAmount: StateFlow<Int> = _finalCreditAmount

    //Refund
    private val _mqttRefundTransactionFinishSuccessFlag = MutableStateFlow(false)
    val mqttRefundTransactionFinishSuccessFlag: StateFlow<Boolean> get() = _mqttRefundTransactionFinishSuccessFlag

    private val _mqttRefundTransactionFinishSuccessMsg = MutableStateFlow("")
    val mqttRefundTransactionFinishSuccessMsg: StateFlow<String> get() = _mqttRefundTransactionFinishSuccessMsg

    private val _mqttRefundTransactionFinishFailFlag = MutableStateFlow(false)
    val mqttRefundTransactionFinishFailFlag: StateFlow<Boolean> get() = _mqttRefundTransactionFinishFailFlag

    private val _mqttRefundTransactionFinishFailMsg = MutableStateFlow("")
    val mqttRefundTransactionFinishFailMsg: StateFlow<String> get() = _mqttRefundTransactionFinishFailMsg


    // Broadcast receiver for MQTT messages
    private val mqttReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { handleBroadcast(it) }
        }
    }

    private val _account = MutableStateFlow("")
    private val _topic = MutableStateFlow("")
    private var _host = MutableStateFlow("")
    private var _port = MutableStateFlow(0)
    private val _subscriptionTopic = MutableStateFlow("")
    private  val _depositMac = MutableStateFlow("")
    private val _deviceID = MutableStateFlow("")


    init {
        loadLoginPreferences()
        registerBroadcastReceivers()
    }

    fun loadLoginPreferences() {
        viewModelScope.launch {
            _account.value = credentialRepository.getUsername() ?: ""
            coroutineScope{
                userPreferences.getMqttTopicValue.firstOrNull().let { topic ->
                    _topic.value = topic ?: ""
                }
                userPreferences.getMqttHostValue.firstOrNull().let { host ->
                    _host.value = host ?: ""
                }
                userPreferences.getMqttPortValue.firstOrNull().let { port ->
                    _port.value = port ?: 0
                }
                userPreferences.getSubscriptionTopicValue.firstOrNull()?.let { value ->
                    _subscriptionTopic.value = value
                }
                userPreferences.getDepositMacAddressValue.firstOrNull().let { value ->
                    _depositMac.value = value ?: ""
                }
                userPreferences.getUUIDValue.firstOrNull().let { value ->
                    _deviceID.value = value ?: ""
                }
            }
        }
    }

    private fun registerBroadcastReceivers() {
        val context = getApplication<Application>()
        val filter = IntentFilter().apply {
            addAction(MqttHelper.TRANSACTION_RECEIPTED)
            addAction(MqttHelper.TRANSACTION_RESULT)
            addAction(MqttHelper.REQUEST_TRANSACTION_FINISHED)
            addAction(MqttHelper.REFUND_TRANSACTION)
            addAction(MqttHelper.REFUND_TRANSACTION_RECEIPTED)
            addAction(MqttHelper.REQUEST_REFUND_TRANSACTION_FINISHED)
            addAction(MqttHelper.CHANNEL_BINDING)
        }
        ContextCompat.registerReceiver(
            context,
            mqttReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    /**
     * Connect to MQTT server
     */
    fun connect() {
        viewModelScope.launch {
            userPreferences.getMqttTopicValue.firstOrNull().let { topic ->
                _topic.value = topic ?: ""
            }

            mqttHelper.apply {
                setClientId(0)
                setMyTopic(_topic.value)
                setDeviceID(_deviceID.value)
                setSubscriptionTopic(_subscriptionTopic.value)
                setDepositMacAddress(_depositMac.value)
                connectServer(credentialRepository.getMqttUsername() ?: "", credentialRepository.getMqttPassword() ?: "", _host.value, _port.value)
            }
        }
    }

    /**
     * Disconnect from MQTT server
     */
    fun disconnect() {
        viewModelScope.launch {
            mqttHelper.disconnectServer()
            _transactionState.value = MqttModel.TransactionState.Idle
//            _refundState.value = RefundState.Idle
        }
    }

    private fun handleBroadcast(intent: Intent) {
        when (intent.action) {
            MqttHelper.TRANSACTION_RECEIPTED -> handleTransactionReceipted(intent)
            MqttHelper.TRANSACTION_RESULT -> handleTransactionResult(intent)
            MqttHelper.REQUEST_TRANSACTION_FINISHED -> handleTransactionFinished(intent)
//            MqttHelper.REFUND_TRANSACTION -> handleRefundResponse(intent)
//            MqttHelper.REFUND_TRANSACTION_RECEIPTED -> handleRefundReceipted(intent)
            MqttHelper.REQUEST_REFUND_TRANSACTION_FINISHED -> handleRefundTransactionFinished(intent)
//            MqttHelper.CHANNEL_BINDING -> handleChannelBinding()
        }
    }

    private fun handleTransactionReceipted(intent: Intent) {
        val txState = intent.getIntExtra(MqttHelper.TRANSACTION_RECEIPTED_REQUEST_TX_STATE, 0)
        val txAmt = intent.getIntExtra(MqttHelper.TRANSACTION_RECEIPTED_REQUEST_TX_AMT, 0)
        val responseCode = intent.getStringExtra(MqttHelper.TRANSACTION_RECEIPTED_REQUEST_TX_RESULT_CODE) ?: ""
        val paymentType = intent.getIntExtra(MqttHelper.TRANSACTION_RECEIPTED_REQUEST_PAYMENT_TYPE, 0)

        _mqttTransactionReceiptedSuccessFlag.value = true
        _transactionState.value = MqttModel.TransactionState.Receipted(
            txState = txState,
            txAmt = txAmt,
            responseCode = responseCode,
            paymentType = paymentType
        )
        if (txAmt != null) {
            if (responseCode == "0000") {
                if (txAmt > 0) {
                    _isReceipt.value = true
                    _mqttTransactionReceiptedSuccessFlag.value = true
                    if (paymentType == 1) {
                        _creditAmount.value += txAmt
                        _finalCreditAmount.value = _creditAmount.value
                    }else if (paymentType >= 2 && paymentType <= 3) {
                        _creditAmount.value = txAmt
                        _finalCreditAmount.value = _creditAmount.value
                    }
                }
            }else{
                _mqttTransactionReceiptedFailFlag.value = true
                _mqttTransactionReceiptedFailMsg.value = "TransactionCancellation"
            }

        }else{
            _mqttTransactionReceiptedFailFlag.value = true
            _mqttTransactionReceiptedFailMsg.value = "TransactionCancellation"
        }


    }

    private fun handleTransactionResult(intent: Intent) {
        val preCreditValue = intent.getStringExtra(MqttHelper.TRANSACTION_RESULT_REQUEST_PRE_CREDIT_VALUE)
        val creditValue = intent.getStringExtra(MqttHelper.TRANSACTION_RESULT_REQUEST_CREDIT_VALUE)
        val result = intent.getBooleanExtra(MqttHelper.TRANSACTION_RESULT_REQUEST_RESULT, false)
        val timestamp = intent.getStringExtra(MqttHelper.TRANSACTION_RESULT_REQUEST_TIMESTAMP)

//        _transactionState.value = MqttModel.TransactionState.Success(
//            preCreditValue = preCreditValue,
//            creditValue = creditValue,
//            result = result,
//            timestamp = timestamp
//        )
    }

    private fun handleTransactionFinished(intent: Intent) {
        val timeout = intent.getIntExtra(MqttHelper.TIMEOUT, 0)
        _transactionState.value = MqttModel.TransactionState.Finished(timeout)
        if (timeout == 1) {
            _mqttTransactionFinishFailFlag.value = true
            _mqttTransactionFinishFailMsg.value = "TransactionCancellation"
        }else{
            if (_isReceipt.value) {
                _mqttTransactionFinishSuccessFlag.value = true
                _mqttTransactionFinishSuccessMsg.value = "TransactionSuccessful"

            }else{
                _mqttTransactionFinishFailFlag.value = true
                _mqttTransactionFinishFailMsg.value = "TransactionCancellation"
            }
        }
    }

    //Refund
    private fun handleRefundTransactionFinished(intent: Intent) {
        val resultValue = intent.getBooleanExtra(MqttHelper.REQUEST_REFUND_TRANSACTION_FINISHED_RESULT,false)
        if (resultValue) {
            _mqttRefundTransactionFinishSuccessFlag.value = true
            _mqttRefundTransactionFinishSuccessMsg.value = "RefundSuccess"
        }else{
            _mqttRefundTransactionFinishFailFlag.value = true
            _mqttRefundTransactionFinishFailMsg.value = "TransactionCancellation"
        }
    }


    fun resetMqttTransactionReceiptedSuccessFlag(value: Boolean) {
        _mqttTransactionReceiptedSuccessFlag.value = value
    }

    fun resetMqttTransactionReceiptedFailFlag(value: Boolean) {
        _mqttTransactionReceiptedFailFlag.value = value
    }

    fun resetMqttTransactionFinishSuccessFlag(value: Boolean) {
        _mqttTransactionFinishSuccessFlag.value = value
    }

    fun resetMqttTransactionFinishFailFlag(value: Boolean) {
        _mqttTransactionFinishFailFlag.value = value
    }

    fun resetMqttRefundTransactionFinishSuccessFlag(value: Boolean) {
        _mqttRefundTransactionFinishSuccessFlag.value = value
    }

    fun resetMqttRefundTransactionFinishFailFlag(value: Boolean) {
        _mqttRefundTransactionFinishFailFlag.value = value
    }
}