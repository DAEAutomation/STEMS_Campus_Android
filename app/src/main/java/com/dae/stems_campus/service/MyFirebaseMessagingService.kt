package com.dae.stems_campus.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dae.stems_campus.MainActivity
import com.dae.stems_campus.R
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.PushNotificationRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.replace

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var pushNotificationRepository: PushNotificationRepository

    @Inject
    lateinit var credentialRepository: CredentialRepository

    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    // 建立一個 Service 生命週期的 CoroutineScope
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("DAE_New Token is: $token")

        serviceScope.launch {
            try {

                val result = pushNotificationRepository.sendFCMToken(
                    aFCMToken = token
                )

                when (result) {
                    is BaseRepository.Result.Success -> {
                        Log.d("DAE_FCM", "Token 上傳成功: ${result.data}")
                    }
                    is BaseRepository.Result.Error -> {
                        Log.e("DAE_FCM", "Token 上傳失敗: ${result.message}")
                    }
                    is BaseRepository.Result.Unauthorized -> {
                        Log.e("DAE_FCM", "Token 上傳失敗:")
                    }
                }
            } catch (e: Exception) {
                Log.e("DAE_FCM", "發送 Token 時發生錯誤", e)
            }
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("DAE_FCM","Received message:${remoteMessage.from}")
        Log.d("DAE_FCM","Received message:${remoteMessage.data}")

        // 檢查是否有資料內容
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data

            if (data["title"] != null && data["body"] != null) {
                val title: String
                val body: String
                val subtitle: String

                body = data["body"]?.replace("\\\\n", "\n") ?: ""

                if (data["subtitle"] != null) {
                    subtitle = data["subtitle"]?.replace("\\\\n", "\n") ?: ""
                    println("Notification subtitle: ${data["subtitle"]}")
                    title = "${data["title"]?.replace("\\\\n", "\n")}\n$subtitle"
                } else {
                    subtitle = ""
                    title = data["title"]?.replace("\\\\n", "\n") ?: ""
                }

                makeNotification(title, subtitle, body)
            }
        }

        // 檢查是否有通知內容
        remoteMessage.notification?.let {
            println("Message Notification Body: ${it.body}")
        }
    }

    private fun makeNotification(title: String, subTitle: String, content: String) {
        Log.d("FCM","Title:${title}")
        Log.d("FCM","Body:${content}")

        val channelId = "default_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "預設通知",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "應用程式推播通知"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // 將推播資料傳遞到 Activity
//            data.forEach { (key, value) ->
//                putExtra(key, value)
//            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

//        val notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle(title ?: "新訊息")
//            .setContentText(content ?: "")
//            .setSmallIcon(R.drawable.chargingstation_fun)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}