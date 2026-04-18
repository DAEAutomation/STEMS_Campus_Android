package com.dae.stems_campus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.repository.BaseRepository
import com.dae.stems_campus.data.repository.CredentialRepository
import com.dae.stems_campus.data.repository.HistoryRepository
import com.dae.stems_campus.data.repository.ProfileRepository
import com.dae.stems_campus.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private var historyRepository: HistoryRepository, private val userPreferences: UserPreferencesRepository, private val credentialRepository: CredentialRepository) : ViewModel() {


    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    private val _resWalletHistorySuccessFlag = MutableStateFlow(false)
    val resWalletHistorySuccessFlag: StateFlow<Boolean> get() = _resWalletHistorySuccessFlag

    private val _walletHistoryList = MutableStateFlow<List<HistoryModel.WalletHistory>>(emptyList())
    val walletHistoryList: StateFlow<List<HistoryModel.WalletHistory>> = _walletHistoryList

    private val _showWalletHistoryFailDialogFlag = MutableStateFlow(false)
    val showWalletHistoryFailDialogFlag: StateFlow<Boolean> get() = _showWalletHistoryFailDialogFlag

    private val _showWalletHistoryFailMsg = MutableStateFlow<String?>("")
    val showWalletHistoryFailMsg: StateFlow<String?> = _showWalletHistoryFailMsg


    // 查詢 錢包儲值紀錄
    fun getWalletHistoryAction(startDate: String, endDate: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = historyRepository.getWalletHistory(startDate,endDate)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resWalletHistorySuccessFlag.value = true
                    _walletHistoryList.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showWalletHistoryFailDialogFlag.value = true
                    _showWalletHistoryFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showWalletHistoryFailDialogFlag.value = true
                    _showWalletHistoryFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }


    // 7天前
    fun lastSevenDays(): Pair<String,String>{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 設定日期格式
        val today = LocalDate.now() // 取得今天的日期
        val sixDaysAgo = today.minusDays(6) // 取得六天前的日期

        // 格式化輸出
        val todayStr = today.format(formatter)
        val sixDaysAgoStr = sixDaysAgo.format(formatter)

        println("今天：$todayStr")
        println("六天前：$sixDaysAgoStr")
        return Pair(sixDaysAgoStr,todayStr)
    }

    // 1個月
    fun lastMonth(): Pair<String,String>{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 設定日期格式
        val today = LocalDate.now() // 取得今天的日期
        val oneMonthAgo = today.minusMonths(1) // 取得一個月前的日期

        // 格式化輸出
        val todayStr = today.format(formatter)
        val oneMonthAgoStr = oneMonthAgo.format(formatter)

        println("今天：$todayStr")
        println("一個月前：$oneMonthAgoStr")
        return Pair(oneMonthAgoStr,todayStr)
    }


    // 3個月
    fun lastThreeMonths(): Pair<String,String>{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 設定日期格式
        val today = LocalDate.now() // 取得今天的日期
        val oneMonthAgo = today.minusMonths(3) // 取得一個月前的日期

        // 格式化輸出
        val todayStr = today.format(formatter)
        val threeMonthAgoStr = oneMonthAgo.format(formatter)

        println("今天：$todayStr")
        println("3個月前：$threeMonthAgoStr")
        return Pair(threeMonthAgoStr,todayStr)
    }


    fun resetResWalletHistorySuccessFailDialogFlag(value: Boolean) {
        _resWalletHistorySuccessFlag.value = value
    }

    fun resetShowWalletHistoryFailDialogFlag(value: Boolean) {
        _showWalletHistoryFailDialogFlag.value = value
    }

}