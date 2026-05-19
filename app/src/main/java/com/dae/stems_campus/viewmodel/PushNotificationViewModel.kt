package com.dae.stems_campus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.PushNotificationRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PushNotificationViewModel  @Inject constructor(private val pushNotificationRepository: PushNotificationRepository, private val credentialRepository: CredentialRepository) : ViewModel() {

    private fun sendFCMToken(token: String) {
        viewModelScope.launch {
            when (val result = pushNotificationRepository.sendFCMToken(aFCMToken = token)) {
                is BaseRepository.Result.Success -> {
                    Log.d("DAE_FCM_ViewModel", "Token 上傳成功: ${result.data}")
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_FCM_ViewModel", "Token 上傳失敗: ${result.message}")
                }
                is BaseRepository.Result.Unauthorized -> {
                    Log.d("DAE_FCM_ViewModel", "請重新登入")
                }
            }
        }
    }

    fun fcm() {
        // 取得 FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("DAE_FCM_ViewModel", "FCM Token: $token")
                // 可以將 token 存到 ViewModel 或傳到後端
                sendFCMToken(token)
            } else {
                Log.e("DAE_FCM_ViewModel", "取得 Token 失敗", task.exception)
            }
        }

    }
}