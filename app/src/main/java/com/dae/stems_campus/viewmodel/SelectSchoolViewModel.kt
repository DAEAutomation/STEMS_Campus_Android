package com.dae.stems_campus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.SchoolHostModel
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.DiscoveryRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import com.dae.stems_campus.network.BaseUrlHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectSchoolViewModel @Inject constructor(
    private val discoveryRepository: DiscoveryRepository,
    private val userPreferences: UserPreferencesRepository,
    private val baseUrlHolder: BaseUrlHolder
) : ViewModel() {

    private val _schools = MutableStateFlow<List<SchoolHostModel>>(emptyList())
    val schools: StateFlow<List<SchoolHostModel>> = _schools

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> = _showLoadingView

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _confirmedFlag = MutableStateFlow(false)
    val confirmedFlag: StateFlow<Boolean> = _confirmedFlag

    fun loadSchools() {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = discoveryRepository.getSchoolHosts()) {
                is BaseRepository.Result.Success -> {
                    _schools.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _errorMessage.value = result.message
                }
                else -> Unit
            }
            _showLoadingView.value = false
        }
    }

    fun confirmSchool(host: String) {
        viewModelScope.launch {
            userPreferences.setApiDomainValue(host)
            baseUrlHolder.baseUrl = host
            _confirmedFlag.value = true
        }
    }

    fun resetConfirmedFlag() {
        _confirmedFlag.value = false
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
