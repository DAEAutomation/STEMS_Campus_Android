package com.dae.stems_campus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.ProfileRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private var profileRepository: ProfileRepository, private val userPreferences: UserPreferencesRepository, private val credentialRepository: CredentialRepository) : ViewModel() {


    private val _UUID = MutableStateFlow("")
    val UUID: StateFlow<String> = _UUID

    //生物辨識
    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled

    init {
        loadLoginPreferences()
    }

    private fun loadLoginPreferences() {
        viewModelScope.launch {
            launch {
                userPreferences.getUUIDValue.collectLatest { value ->
                    _UUID.value = value
                }
            }
        }
    }


    fun getBiometricValue() {
        viewModelScope.launch {
            coroutineScope {
                userPreferences.getBiometricValue.firstOrNull()?.let { value ->
                    _isBiometricEnabled.value = value
                }
            }
        }
    }

    //紀錄生物辦辨識是否啟動
    fun updateBiometricValue(value: Boolean) {
        _isBiometricEnabled.value = value
        viewModelScope.launch {
            userPreferences.setBiometricValue(value)
        }
    }
}