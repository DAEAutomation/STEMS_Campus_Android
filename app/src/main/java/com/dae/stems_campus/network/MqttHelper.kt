package com.dae.stems_campus.network

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.concurrent.CompletableFuture
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Modern Kotlin MQTT Helper with Coroutines and Flow support
 */
class MqttHelper private constructor(private val context: Context) {

    private var mqttClient: Mqtt3AsyncClient? = null
    private var clientId: Int = 0
    private var myTopic: String = ""
    private var deviceID: String = "" //UUID
    private var subscriptionTopic: String = ""
    private var depositMac: String = ""

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _subscriptionState = MutableStateFlow(SubscriptionState.NOTYET)
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState

    private val _isReceipt = MutableStateFlow(false)
    val isReceipt: StateFlow<Boolean> = _isReceipt

    companion object {
        // Broadcast actions
        const val RESPONSE_TRANSACTION = "dae.tenant.public.RESPONSE_TRANSACTION"
        const val RESPONSE_RESULT = "dae.tenant.public.RESPONSE_RESULT"
        const val RESPONSE_MESSAGE = "dae.tenant.public.RESPONSE_MESSAGE"
        const val RESPONSE_CREDIT_VALUE = "dae.tenant.public.RESPONSE_CREDIT_VALUE"
        const val RESPONSE_DISPLAY_NAME = "dae.tenant.public.RESPONSE_DISPLAY_NAME"
        const val RESPONSE_RATE = "dae.tenant.public.RESPONSE_RATE"
        const val RESPONSE_MAX_CREDIT_AMOUNT = "dae.tenant.public.RESPONSE_MAX_CREDIT_AMOUNT"
        const val TIMEOUT = "dae.tenant.public.TIMEOUT"
        const val REQUEST_TRANSACTION_FINISHED = "dae.tenant.public.REQUEST_TRANSACTION_FINISHED"
        const val TRANSACTION_RECEIPTED = "dae.tenant.public.TRANSACTION_RECEIPTED"
        const val TRANSACTION_RESULT = "dae.tenant.public.TRANSACTION_RESULT"
        const val TRANSACTION_RESULT_REQUEST_PRE_CREDIT_VALUE = "dae.tenant.public.TRANSACTION_RESULT_REQUEST_PRE_CREDIT_VALUE"
        const val TRANSACTION_RESULT_REQUEST_CREDIT_VALUE = "dae.tenant.public.TRANSACTION_RESULT_REQUEST_CREDIT_VALUE"
        const val TRANSACTION_RESULT_REQUEST_DEPOSIT_NAME = "dae.tenant.public.TRANSACTION_RESULT_REQUEST_DEPOSIT_NAME"
        const val TRANSACTION_RESULT_REQUEST_RESULT = "dae.tenant.public.TRANSACTION_RESULT_REQUEST_RESULT"
        const val TRANSACTION_RESULT_REQUEST_TIMESTAMP = "dae.tenant.public.TRANSACTION_RESULT_REQUEST_TIMESTAMP"
        const val TRANSACTION_RECEIPTED_REQUEST_TX_STATE = "dae.tenant.public.TRANSACTION_RECEIPTED_REQUEST_TX_STATE"
        const val TRANSACTION_RECEIPTED_REQUEST_TX_RESULT_CODE = "dae.tenant.public.TRANSACTION_RECEIPTED_REQUEST_TX_RESULT_CODE"
        const val TRANSACTION_RECEIPTED_REQUEST_TX_AMT = "dae.tenant.public.TRANSACTION_RECEIPTED_REQUEST_TX_AMT"
        const val TRANSACTION_RECEIPTED_REQUEST_PAYMENT_TYPE = "dae.tenant.public.TRANSACTION_RECEIPTED_REQUEST_PAYMENT_TYPE"
        const val REFUND_TRANSACTION = "dae.tenant.public.REFUND_TRANSACTION"
        const val RESPONSE_REFUND_RESULT = "dae.tenant.public.RESPONSE_REFUND_RESULT"
        const val RESPONSE_REFUND_MESSAGE = "dae.tenant.public.RESPONSE_REFUND_MESSAGE"
        const val RESPONSE_REFUND_CREDIT_VALUE = "dae.tenant.public.RESPONSE_REFUND_CREDIT_VALUE"
        const val RESPONSE_REFUND_DISPLAY_NAME = "dae.tenant.public.RESPONSE_REFUND_DISPLAY_NAME"
        const val RESPONSE_REFUND_RATE = "dae.tenant.public.RESPONSE_REFUND_RATE"
        const val RESPONSE_REFUND_AMOUNT = "dae.tenant.public.RESPONSE_REFUND_AMOUNT"
        const val TRANSACTION_RESULT_REQUEST_REFUND_DEPOSIT_NAME = "dae.tenant.public.TRANSACTION_RESULT_REQUEST_DEPOSIT_NAME"
        const val REFUND_TRANSACTION_RECEIPTED = "dae.tenant.public.REFUND_TRANSACTION_RECEIPTED"
        const val REFUND_TRANSACTION_RECEIPTED_STATE = "dae.tenant.public.REFUND_TRANSACTION_RECEIPTED_STATE"
        const val REFUND_TRANSACTION_RECEIPTED_RESPONSE_CODE = "dae.tenant.public.REFUND_TRANSACTION_RECEIPTED_RESPONSE_CODE"
        const val REFUND_TRANSACTION_RECEIPTED_AMT = "dae.tenant.public.REFUND_TRANSACTION_RECEIPTED_AMT"
        const val REFUND_TRANSACTION_RECEIPTED_PAYMENT_TYPE = "dae.tenant.public.REFUND_TRANSACTION_RECEIPTED_PAYMENT_TYPE"
        const val REQUEST_REFUND_TRANSACTION_FINISHED = "dae.tenant.public.REQUEST_REFUND_TRANSACTION_FINISHED"
        const val REQUEST_REFUND_TRANSACTION_FINISHED_RESULT = "dae.tenant.public.REQUEST_REFUND_TRANSACTION_FINISHED_RESULT"
        const val REQUEST_REFUND_TRANSACTION_FINISHED_USERNAME = "dae.tenant.public.REQUEST_REFUND_TRANSACTION_FINISHED_USERNAME"
        const val REQUEST_REFUND_TRANSACTION_FINISHED_DEVICE_CODE = "dae.tenant.public.REQUEST_REFUND_TRANSACTION_FINISHED_DEVICE_CODE"
        const val CHANNEL_BINDING = "dae.tenant.public.CHANNEL_BINDING"

        @Volatile
        private var instance: MqttHelper? = null

        fun getInstance(context: Context): MqttHelper {
            return instance ?: synchronized(this) {
                instance ?: MqttHelper(context.applicationContext).also { instance = it }
            }
        }
    }

    enum class ConnectionState {
        DISCONNECTED, CONNECTING, CONNECTED, ERROR
    }

    enum class SubscriptionState {
        NOTYET,SUCCESS,FAIL
    }

    // Getters and Setters
    fun setClientId(id: Int) {
        clientId = id
    }

    fun setMyTopic(topic: String) {
        myTopic = topic
    }

    fun setDeviceID(value: String) {
        deviceID = value
    }

    fun setSubscriptionTopic(value: String) {
        subscriptionTopic = value
    }

    fun setDepositMacAddress(value: String) {
        depositMac = value
    }


    fun isConnected(): Boolean = mqttClient?.state?.isConnected == true

    /**
     * Connect to MQTT server using HiveMQ client
     */
    suspend fun connectServer(
        username: String,
        password: String,
        host: String,
        port: Int = 18830
    ) = suspendCancellableCoroutine { continuation ->

        if (mqttClient != null && isConnected()) {
            Log.d("DAE_Develop_Mqtt", "Already connected")
            continuation.resume(Unit)
            return@suspendCancellableCoroutine
        }

        _connectionState.value = ConnectionState.CONNECTING

        try {
            // Create HiveMQ MQTT client
            val client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("$clientId${System.currentTimeMillis()}")
                .serverHost(host)
                .serverPort(port)
                .automaticReconnectWithDefaultConfig()
                .buildAsync()

            // Connect with credentials
            client.connectWith()
                .simpleAuth()
                .username(username)
                .password(password.toByteArray(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .cleanSession(false)
                .send()
                .whenComplete { _, throwable ->
                    if (throwable != null) {
                        Log.e("DAE_Develop_Mqtt", "Connection failed", throwable)
                        _connectionState.value = ConnectionState.ERROR
                        continuation.resumeWithException(throwable)
                    } else {
                        Log.d("DAE_Develop_Mqtt", "Connected successfully")
                        mqttClient = client
                        _connectionState.value = ConnectionState.CONNECTED
                        _isReceipt.value = false

                        // Subscribe to topics if MAC address is available
                        subscriptionTopic?.let { subscriptionTopic ->
                            subscribeToTopics(client, subscriptionTopic)
                        }


                        continuation.resume(Unit)
                    }
                }

        } catch (e: Exception) {
            Log.e("DAE_Develop_Mqtt", "Error creating client", e)
            _connectionState.value = ConnectionState.ERROR
            continuation.resumeWithException(e)
        }
    }

    /**
     * Subscribe to MQTT topics
     */
    private fun subscribeToTopics(client: Mqtt3AsyncClient, subTopics: String) {
        // Subscribe to all topic
        client.subscribeWith()
            .topicFilter(subTopics)
            .callback { publish ->
                handleMessage(publish.topic.toString(), publish.payloadAsBytes)
            }
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    _subscriptionState.value = SubscriptionState.FAIL
                    Log.e("DAE_Develop_Mqtt", "Subscribe to all failed", throwable)
                } else {
                    _subscriptionState.value = SubscriptionState.SUCCESS
                    Log.d("DAE_Develop_Mqtt", "Subscribed to $subTopics")
                }
            }
    }

    /**
     * Disconnect from MQTT server
     */
    suspend fun disconnectServer() = suspendCancellableCoroutine { continuation ->
        mqttClient?.let { client ->
            // 檢查是否已連接
            if (client.state.isConnected) {
                client.disconnect()
                    .whenComplete { _, throwable ->
                        if (throwable != null) {
                            Log.e("DAE_Develop_Mqtt", "Disconnect failed", throwable)
                            continuation.resumeWithException(throwable)
                        } else {
                            Log.d("DAE_Develop_Mqtt", "Disconnected successfully")
                            mqttClient = null
                            _connectionState.value = ConnectionState.DISCONNECTED
                            continuation.resume(Unit)
                        }
                    }
            } else {
                // 如果沒有連接，直接清理狀態
                Log.d("DAE_Develop_Mqtt", "Client not connected, cleaning up")
                mqttClient = null
                _connectionState.value = ConnectionState.DISCONNECTED
                continuation.resume(Unit)
            }
        } ?: run {
            _connectionState.value = ConnectionState.DISCONNECTED
            continuation.resume(Unit)
        }
    }


    /**
     * Handle incoming MQTT message
     */
    private fun handleMessage(topic: String, payloadBytes: ByteArray) {
        try {
            Log.d("DAE_Develop_Mqtt", "Topic $topic")
            Log.d("DAE_Develop_Mqtt", "myTopic $myTopic/$depositMac/$deviceID/server")

            val currentMac = depositMac ?: return
            val message = String(payloadBytes, StandardCharsets.UTF_8)

            Log.d("DAE_Develop_Mqtt", "Message arrived from $topic: $message")

            val intent = Intent()

            when (topic) {
                // 交易回執
                "$myTopic/$currentMac/$deviceID/server/request/transaction-receipted" -> {
                    handleTransactionReceipted(message, intent)
                }
                // 交易結果
                "$myTopic/$currentMac/$deviceID/server/request/transaction-result" -> {
                    handleTransactionResult(message, intent)
                }
                // 交易完成
                "$myTopic/$currentMac/$deviceID/server/request/transaction-finished" -> {
                    handleTransactionFinished(message, intent)
                }
                "$myTopic/$currentMac/$deviceID/server/response/refund-transaction" -> {
                    handleRefundTransaction(message, intent)
                }
                "$myTopic/$currentMac/$deviceID/server/request/refund-transaction-receipted" -> {
                    handleRefundReceipted(message, intent)
                }
                "$myTopic/$currentMac/$deviceID/server/request/refund-transaction-finished" -> {
                    handleRefundFinished(message, intent)
                }
                "$myTopic/$currentMac/$deviceID/server/request/refund-transaction-result" -> {
                    handleRefundResult(message)
                }
                "$myTopic/$currentMac/$deviceID/server/request/channel-binding" -> {
                    handleChannelBinding(intent)
                }
            }

        } catch (e: Exception) {
            Log.e("HiveMqttHelper", "Error handling message", e)
        }
    }


    private fun handleTransactionResponse(content: String, intent: Intent) {
        val json = JSONObject(content)
        intent.apply {
            action = RESPONSE_TRANSACTION
            setPackage(context.packageName)
            putExtra(RESPONSE_RESULT, json.getBoolean("result"))
            putExtra(RESPONSE_MESSAGE, json.getString("message"))

            if (json.getBoolean("result") && json.has("data")) {
                val data = json.getJSONObject("data")
                val device = data.getJSONObject("device")
                putExtra(RESPONSE_CREDIT_VALUE, device.getString("credit-value"))
                putExtra(RESPONSE_DISPLAY_NAME, device.getString("display-name"))
                putExtra(RESPONSE_RATE, device.getString("rate"))
                putExtra(TRANSACTION_RESULT_REQUEST_DEPOSIT_NAME, data.getString("deposit-name"))
                putExtra(RESPONSE_MAX_CREDIT_AMOUNT, device.getInt("max-credit-amount").toString())
            }else{
                println("fsdffsfdfsdff")
            }
        }
        context.sendBroadcast(intent)
    }

    private fun handleTransactionReceipted(content: String, intent: Intent) {
        val json = JSONObject(content)
        intent.apply {
            action = TRANSACTION_RECEIPTED
            setPackage(context.packageName)
            putExtra(TRANSACTION_RECEIPTED_REQUEST_TX_STATE, json.getInt("tx-state"))
            putExtra(TRANSACTION_RECEIPTED_REQUEST_TX_AMT, json.getInt("tx-amt"))
            putExtra(TRANSACTION_RECEIPTED_REQUEST_TX_RESULT_CODE, json.getString("tx-response-code"))
            putExtra(TRANSACTION_RECEIPTED_REQUEST_PAYMENT_TYPE, json.optInt("payment-type"))
        }
        context.sendBroadcast(intent)
    }

    private fun handleTransactionResult(content: String, intent: Intent) {
        val json = JSONObject(content)
        intent.apply {
            action = TRANSACTION_RESULT
            setPackage(context.packageName)
            if (json.has("data")) {
                val data = json.getJSONObject("data")
                putExtra(TRANSACTION_RESULT_REQUEST_PRE_CREDIT_VALUE, data.getString("pre-credit-value"))
                putExtra(TRANSACTION_RESULT_REQUEST_CREDIT_VALUE, data.getString("credit-value"))
                putExtra(TRANSACTION_RESULT_REQUEST_RESULT, json.getBoolean("result"))
                putExtra(TRANSACTION_RESULT_REQUEST_TIMESTAMP, json.getString("timestamp"))
            }
        }
        context.sendBroadcast(intent)
    }

    private fun handleTransactionFinished(content: String, intent: Intent) {
        val json = JSONObject(content)
        intent.apply {
            action = REQUEST_TRANSACTION_FINISHED
            setPackage(context.packageName)
            putExtra(TIMEOUT, json.optInt("timeout"))
        }
        context.sendBroadcast(intent)
    }

    private fun handleRefundTransaction(content: String, intent: Intent) {
        _isReceipt.value = false
        val json = JSONObject(content)
        intent.apply {
            action = REFUND_TRANSACTION
            setPackage(context.packageName)
            putExtra(RESPONSE_REFUND_RESULT, json.getBoolean("result"))
            putExtra(RESPONSE_REFUND_MESSAGE, json.getString("message"))

            if (json.getBoolean("result")) {
                val data = json.getJSONObject("data")
                val device = data.getJSONObject("device")
                putExtra(RESPONSE_REFUND_CREDIT_VALUE, device.optString("credit-value"))
                putExtra(RESPONSE_REFUND_DISPLAY_NAME, device.optString("display-name"))
                putExtra(RESPONSE_REFUND_RATE, device.optString("rate"))
                putExtra(TRANSACTION_RESULT_REQUEST_REFUND_DEPOSIT_NAME, data.optString("deposit-name"))
                putExtra(RESPONSE_REFUND_AMOUNT, device.optInt("amount"))
            }
        }
        context.sendBroadcast(intent)
    }

    private fun handleRefundReceipted(content: String, intent: Intent) {
        _isReceipt.value = true
        val json = JSONObject(content)
        intent.apply {
            action = REFUND_TRANSACTION_RECEIPTED
            setPackage(context.packageName)
            putExtra(REFUND_TRANSACTION_RECEIPTED_AMT, json.optInt("refund-amt"))
            putExtra(REFUND_TRANSACTION_RECEIPTED_STATE, json.optInt("refund-state"))
            putExtra(REFUND_TRANSACTION_RECEIPTED_RESPONSE_CODE, json.optString("refund-response-code"))
            putExtra(REFUND_TRANSACTION_RECEIPTED_PAYMENT_TYPE, json.optInt("payment-type"))
        }
        context.sendBroadcast(intent)
    }

    private fun handleRefundFinished(content: String, intent: Intent) {
        val json = JSONObject(content)
        intent.apply {
            action = REQUEST_REFUND_TRANSACTION_FINISHED
            setPackage(context.packageName)
            putExtra(REQUEST_REFUND_TRANSACTION_FINISHED_RESULT, json.optBoolean("result"))
        }
        context.sendBroadcast(intent)
    }

    private fun handleRefundResult(content: String) {
        Log.d("HiveMqttHelper", "Refund result: $content")
        // Note: In HiveMQ, we don't disconnect here as it's handled by coroutines
    }

    private fun handleChannelBinding(intent: Intent) {
        intent.apply {
            action = CHANNEL_BINDING
            setPackage(context.packageName)
        }
        context.sendBroadcast(intent)
    }
}
