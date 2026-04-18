package com.dae.stems_campus.data.model

class MqttModel {

    // Sealed classes for state management
    sealed class TransactionState {
        object Idle : TransactionState()
        object Processing : TransactionState()
        data class ResponseReceived(
            val creditValue: String?,
            val displayName: String?,
            val rate: String?,
            val depositName: String?,
            val maxCreditAmount: String?
        ) : TransactionState()
        data class Receipted(
            val txState: Int,
            val txAmt: Int,
            val responseCode: String,
            val paymentType: Int
        ) : TransactionState()
        data class Success(
            val preCreditValue: String?,
            val creditValue: String?,
            val result: Boolean,
            val timestamp: String?
        ) : TransactionState()
        data class Finished(val timeout: Int) : TransactionState()
        data class Error(val message: String) : TransactionState()
    }
}