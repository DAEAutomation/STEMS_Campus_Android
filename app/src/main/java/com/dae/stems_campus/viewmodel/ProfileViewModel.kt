package com.dae.stems_campus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.ProfileRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private var profileRepository: ProfileRepository, private val userPreferences: UserPreferencesRepository, private val credentialRepository: CredentialRepository) : ViewModel() {

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    private val _resGetProfileInfoSuccessFlag = MutableStateFlow(false)
    val resGetProfileInfoSuccessFlag: StateFlow<Boolean> get() = _resGetProfileInfoSuccessFlag

    private val _profileInfo = MutableStateFlow<ProfileModel.ProfileData?>(null)
    val profileInfo: StateFlow<ProfileModel.ProfileData?> = _profileInfo

    private val _showGetProfileInfoFailDialogFlag = MutableStateFlow(false)
    val showGetProfileInfoFailDialogFlag: StateFlow<Boolean> get() = _showGetProfileInfoFailDialogFlag

    private val _showGetProfileInfoFailMsg = MutableStateFlow<String?>("")
    val showGetProfileInfoFailMsg: StateFlow<String?> = _showGetProfileInfoFailMsg


    // 查詢Profile
    fun getProfileInfoAction() {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = profileRepository.getProfileData()) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resGetProfileInfoSuccessFlag.value = true
                    _profileInfo.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showGetProfileInfoFailDialogFlag.value = true
                    _showGetProfileInfoFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showGetProfileInfoFailDialogFlag.value = true
                    _showGetProfileInfoFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }


    fun resetShowGetProfileInfoFailDialogFlag(value: Boolean) {
        _showGetProfileInfoFailDialogFlag.value = value
    }
}