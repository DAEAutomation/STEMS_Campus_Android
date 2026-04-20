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

    private val _resHistorySuccessFlag = MutableStateFlow(false)
    val resHistorySuccessFlag: StateFlow<Boolean> get() = _resHistorySuccessFlag

    private val _showHistoryFailDialogFlag = MutableStateFlow(false)
    val showHistoryFailDialogFlag: StateFlow<Boolean> get() = _showHistoryFailDialogFlag

    private val _showHistoryFailMsg = MutableStateFlow<String?>("")
    val showHistoryFailMsg: StateFlow<String?> = _showHistoryFailMsg

    private val _walletHistoryList = MutableStateFlow<List<HistoryModel.WalletHistory>>(emptyList())
    val walletHistoryList: StateFlow<List<HistoryModel.WalletHistory>> = _walletHistoryList
    var walletHistoryDetail: HistoryModel.WalletHistory? = null

    private val _hoursHistoryList = MutableStateFlow<List<HistoryModel.HoursHistory>>(emptyList())
    val hoursHistoryList: StateFlow<List<HistoryModel.HoursHistory>> = _hoursHistoryList

    private val _classroomHistoryList = MutableStateFlow<List<HistoryModel.ClassroomHistory>>(emptyList())
    val classroomHistoryList: StateFlow<List<HistoryModel.ClassroomHistory>> = _classroomHistoryList
    var classroomHistoryDetail: HistoryModel.ClassroomHistory? = null

    private val _dormitoryHistoryList = MutableStateFlow<List<HistoryModel.DormitoryHistory>>(emptyList())
    val dormitoryHistoryList: StateFlow<List<HistoryModel.DormitoryHistory>> = _dormitoryHistoryList
    var dormitoryHistoryDetail: HistoryModel.DormitoryHistory? = null


    // 查詢 錢包儲值紀錄
    fun getWalletHistoryAction(startDate: String, endDate: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = historyRepository.getWalletHistory(startDate,endDate)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resHistorySuccessFlag.value = true
                    _walletHistoryList.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showHistoryFailDialogFlag.value = true
                    _showHistoryFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showHistoryFailDialogFlag.value = true
                    _showHistoryFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 查詢 時數發給紀錄
    fun getHoursHistoryAction(startDate: String, endDate: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = historyRepository.getHoursHistory(startDate,endDate)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resHistorySuccessFlag.value = true
                    _hoursHistoryList.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showHistoryFailDialogFlag.value = true
                    _showHistoryFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showHistoryFailDialogFlag.value = true
                    _showHistoryFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 查詢 教室使用紀錄
    fun getClassroomHistoryAction(startDate: String, endDate: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = historyRepository.getClassroomHistory(startDate,endDate)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resHistorySuccessFlag.value = true
                    _classroomHistoryList.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showHistoryFailDialogFlag.value = true
                    _showHistoryFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showHistoryFailDialogFlag.value = true
                    _showHistoryFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    // 查詢 宿舍使用紀錄
    fun getDormitoryHistoryAction(startDate: String, endDate: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = historyRepository.getDormitoryHistory(startDate,endDate)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resHistorySuccessFlag.value = true
                    _dormitoryHistoryList.value = result.data
                }
                is BaseRepository.Result.Error -> {
                    _showLoadingView.value = false
                    _showHistoryFailDialogFlag.value = true
                    _showHistoryFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showHistoryFailDialogFlag.value = true
                    _showHistoryFailMsg.value = "PleaseReLogin"
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


    fun resetResHistorySuccessFailDialogFlag(value: Boolean) {
        _resHistorySuccessFlag.value = value
    }

    fun resetShowHistoryFailDialogFlag(value: Boolean) {
        _showHistoryFailDialogFlag.value = value
    }

}