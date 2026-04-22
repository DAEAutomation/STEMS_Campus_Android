package com.dae.stems_campus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.data.model.NotificationsModel
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.NotificationRepository
import com.dae.stems_campus.data.repository.ProfileRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private var notificationRepository: NotificationRepository, private val userPreferences: UserPreferencesRepository, private val credentialRepository: CredentialRepository) : ViewModel() {

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    private val _resGetNotificationSuccessFlag = MutableStateFlow(false)
    val resGetNotificationSuccessFlag: StateFlow<Boolean> get() = _resGetNotificationSuccessFlag

    private val _showGetNotificationFailDialogFlag = MutableStateFlow(false)
    val showGetNotificationFailDialogFlag: StateFlow<Boolean> get() = _showGetNotificationFailDialogFlag

    private val _showGetNotificationFailMsg = MutableStateFlow<String?>("")
    val showGetNotificationFailMsg: StateFlow<String?> = _showGetNotificationFailMsg

    private val _notificationList = MutableStateFlow<List<NotificationsModel.NotificationsData>>(emptyList())
    val notificationList: StateFlow<List<NotificationsModel.NotificationsData>> = _notificationList
    var notificationDetail: NotificationsModel.NotificationsData? = null


    fun getNotificationAction() {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = notificationRepository.getNotification()) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resGetNotificationSuccessFlag.value = true
                    _notificationList.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showGetNotificationFailDialogFlag.value = true
                    _showGetNotificationFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showGetNotificationFailDialogFlag.value = true
                    _showGetNotificationFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    fun resetResGetNotificationSuccessFailDialogFlag(value: Boolean) {
        _resGetNotificationSuccessFlag.value = value
    }

    fun resetShowGetNotificationFailDialogFlag(value: Boolean) {
        _showGetNotificationFailDialogFlag.value = value
    }

}