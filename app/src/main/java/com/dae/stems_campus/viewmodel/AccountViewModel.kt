package com.dae.stems_campus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dae.stems_campus.data.repository.AccountRepository
import com.dae.stems_campus.data.repository.BaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private var accountRepository: AccountRepository) : ViewModel() {

    private val _showLoadingView = MutableStateFlow(false)
    val showLoadingView: StateFlow<Boolean> get() = _showLoadingView

    //註冊Email
    private val _emailText = MutableStateFlow("")
    val emailText: StateFlow<String> = _emailText

    private val _resRegisterSendEmailSuccessFlag = MutableStateFlow(false)
    val resRegisterSendEmailSuccessFlag: StateFlow<Boolean> get() = _resRegisterSendEmailSuccessFlag

    private val _showRegisterSendEmailFailDialogFlag = MutableStateFlow(false)
    val showRegisterSendEmailFailDialogFlag: StateFlow<Boolean> get() = _showRegisterSendEmailFailDialogFlag

    private val _showRegisterSendEmailFailMsg = MutableStateFlow<String?>("")
    val showRegisterSendEmailFailMsg: StateFlow<String?> = _showRegisterSendEmailFailMsg

    private val _showRegisterEmailInputFailFlag = MutableStateFlow(false)
    val showRegisterEmailInputFailFlag: StateFlow<Boolean> get() = _showRegisterEmailInputFailFlag

    private val _showRegisterEmailInputFailMsg = MutableStateFlow<String?>("")
    val showRegisterEmailInputFailMsg: StateFlow<String?> = _showRegisterEmailInputFailMsg

    //忘記密碼Email
    private val _resForgotPwSendEmailSuccessFlag = MutableStateFlow(false)
    val resForgotPwSendEmailSuccessFlag: StateFlow<Boolean> get() = _resForgotPwSendEmailSuccessFlag

    private val _showForgotPwSendEmailFailDialogFlag = MutableStateFlow(false)
    val showForgotPwSendEmailFailDialogFlag: StateFlow<Boolean> get() = _showForgotPwSendEmailFailDialogFlag

    private val _showForgotPwSendEmailFailMsg = MutableStateFlow<String?>("")
    val showForgotPwSendEmailFailMsg: StateFlow<String?> = _showForgotPwSendEmailFailMsg

    private val _showForgotPwEmailInputFailFlag = MutableStateFlow(false)
    val showForgotPwEmailInputFailFlag: StateFlow<Boolean> get() = _showForgotPwEmailInputFailFlag

    private val _showForgotPwEmailInputFailMsg = MutableStateFlow<String?>("")
    val showForgotPwEmailInputFailMsg: StateFlow<String?> = _showForgotPwEmailInputFailMsg

    //註冊驗證碼
    private val _verificationCode1 = MutableStateFlow("")
    val verificationCode1: StateFlow<String> = _verificationCode1

    private val _verificationCode2 = MutableStateFlow("")
    val verificationCode2: StateFlow<String> = _verificationCode2

    private val _verificationCode3 = MutableStateFlow("")
    val verificationCode3: StateFlow<String> = _verificationCode3

    private val _verificationCode4 = MutableStateFlow("")
    val verificationCode4: StateFlow<String> = _verificationCode4

    private val _verifiedToken = MutableStateFlow("")
    val verifiedToken: StateFlow<String> = _verifiedToken

    private val _resVerifyCodeSuccessFlag = MutableStateFlow(false)
    val resVerifyCodeSuccessFlag: StateFlow<Boolean> get() = _resVerifyCodeSuccessFlag

    private val _showVerifyCodeFailDialogFlag = MutableStateFlow(false)
    val showVerifyCodeFailDialogFlag: StateFlow<Boolean> get() = _showVerifyCodeFailDialogFlag

    private val _showVerifyCodeFailMsg = MutableStateFlow<String?>("")
    val showVerifyCodeFailMsg: StateFlow<String?> = _showVerifyCodeFailMsg

    private val _showVerifyCodeInputFailFlag = MutableStateFlow(false)
    val showVerifyCodeInputFailFlag: StateFlow<Boolean> get() = _showVerifyCodeInputFailFlag

    private val _showVerifyCodeInputFailMsg = MutableStateFlow<String?>("")
    val showVerifyCodeInputFailMsg: StateFlow<String?> = _showVerifyCodeInputFailMsg

    //驗證碼重發倒數
    private val _remainingTime = MutableStateFlow(0)
    val remainingTime: StateFlow<Int> = _remainingTime

    private var countdownJob: Job? = null

    private val _resReSendSuccessFlag = MutableStateFlow(false)
    val resReSendSuccessFlag: StateFlow<Boolean> get() = _resReSendSuccessFlag

    private val _showReSendFailDialogFlag = MutableStateFlow(false)
    val showReSendFailDialogFlag: StateFlow<Boolean> get() = _showReSendFailDialogFlag

    private val _showReSendFailMsg = MutableStateFlow<String?>("")
    val showReSendFailMsg: StateFlow<String?> = _showReSendFailMsg

    //學生資訊
    private val _studentIdText = MutableStateFlow("")
    val studentIdText: StateFlow<String> = _studentIdText

    private val _nameText = MutableStateFlow("")
    val nameText: StateFlow<String> = _nameText

    private val _phoneText = MutableStateFlow("")
    val phoneText: StateFlow<String> = _phoneText

    private val _collegeText = MutableStateFlow("")
    val collegeText: StateFlow<String> = _collegeText

    private val _departmentText = MutableStateFlow("")
    val departmentText: StateFlow<String> = _departmentText

    private val _studentTypeText = MutableStateFlow("")
    val studentTypeText: StateFlow<String> = _studentTypeText

    private val _resGetStudentInfoSuccessFlag = MutableStateFlow(false)
    val resGetStudentInfoSuccessFlag: StateFlow<Boolean> get() = _resGetStudentInfoSuccessFlag

    private val _showGetStudentInfoFailDialogFlag = MutableStateFlow(false)
    val showGetStudentInfoFailDialogFlag: StateFlow<Boolean> get() = _showGetStudentInfoFailDialogFlag

    private val _showGetStudentInfoFailMsg = MutableStateFlow<String?>("")
    val showGetStudentInfoFailMsg: StateFlow<String?> = _showGetStudentInfoFailMsg

    private val _isStudentVerified = MutableStateFlow(false)
    val isStudentVerified: StateFlow<Boolean> get() = _isStudentVerified

    //設定密碼/註冊
    private val _passwordText = MutableStateFlow("")
    val passwordText: StateFlow<String> = _passwordText

    private val _confirmPasswordText = MutableStateFlow("")
    val confirmPasswordText: StateFlow<String> = _confirmPasswordText

    private val _resRegisterInfoSuccessFlag = MutableStateFlow(false)
    val resRegisterInfoSuccessFlag: StateFlow<Boolean> get() = _resRegisterInfoSuccessFlag

    private val _showRegisterInfoFailDialogFlag = MutableStateFlow(false)
    val showRegisterInfoFailDialogFlag: StateFlow<Boolean> get() = _showRegisterInfoFailDialogFlag

    private val _showRegisterInfoFailMsg = MutableStateFlow<String?>("")
    val showRegisterInfoFailMsg: StateFlow<String?> = _showRegisterInfoFailMsg

    private val _showRegisterInfoInputFailTag = MutableStateFlow(0)
    val showRegisterInfoInputFailTag: StateFlow<Int> = _showRegisterInfoInputFailTag

    private val _showRegisterInfoInputFailMsg = MutableStateFlow<String?>("")
    val showRegisterInfoInputFailMsg: StateFlow<String?> = _showRegisterInfoInputFailMsg

    //重設密碼
    private val _resResetPasswordSuccessFlag = MutableStateFlow(false)
    val resResetPasswordSuccessFlag: StateFlow<Boolean> get() = _resResetPasswordSuccessFlag

    private val _showResetPasswordFailDialogFlag = MutableStateFlow(false)
    val showResetPasswordFailDialogFlag: StateFlow<Boolean> get() = _showResetPasswordFailDialogFlag

    private val _showResetPasswordFailMsg = MutableStateFlow<String?>("")
    val showResetPasswordFailMsg: StateFlow<String?> = _showResetPasswordFailMsg

    private val _showResetPasswordInputFailTag = MutableStateFlow(0)
    val showResetPasswordInputFailTag: StateFlow<Int> = _showResetPasswordInputFailTag

    private val _showResetPasswordInputFailMsg = MutableStateFlow<String?>("")
    val showResetPasswordInputFailMsg: StateFlow<String?> = _showResetPasswordInputFailMsg


    //註冊發送Email
    fun registerSendEmailAction(aEmail: String) {
        viewModelScope.launch {
            if (aEmail.isEmpty()) {
                _showRegisterEmailInputFailFlag.value = true
                _showRegisterEmailInputFailMsg.value = "EmailNotEntered"
                return@launch
            }
            if (!isEmailValid(aEmail)) {
                _showRegisterEmailInputFailFlag.value = true
                _showRegisterEmailInputFailMsg.value = "EmailFormatInvalid"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = accountRepository.registerSendEmail(aEmail)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resRegisterSendEmailSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "registerSendEmail Res ->${result.message}")
                    _showLoadingView.value = false
                    _showRegisterSendEmailFailDialogFlag.value = true
                    _showRegisterSendEmailFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showRegisterSendEmailFailDialogFlag.value = true
                    _showRegisterSendEmailFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    //忘記密碼發送Email
    fun forgotPwSendEmailAction(aEmail: String) {
        viewModelScope.launch {
            if (aEmail.isEmpty()) {
                _showForgotPwEmailInputFailFlag.value = true
                _showForgotPwEmailInputFailMsg.value = "EmailNotEntered"
                return@launch
            }
            if (!isEmailValid(aEmail)) {
                _showForgotPwEmailInputFailFlag.value = true
                _showForgotPwEmailInputFailMsg.value = "EmailFormatInvalid"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = accountRepository.forgotPwSendEmail(aEmail)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resForgotPwSendEmailSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "forgotPwSendEmail Res ->${result.message}")
                    _showLoadingView.value = false
                    _showForgotPwSendEmailFailDialogFlag.value = true
                    _showForgotPwSendEmailFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showForgotPwSendEmailFailDialogFlag.value = true
                    _showForgotPwSendEmailFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    //驗證碼重發
    fun reSendVerificationAction(aEmail: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = accountRepository.registerSendEmail(aEmail)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resReSendSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "reSendVerification Res ->${result.message}")
                    _showLoadingView.value = false
                    _showReSendFailDialogFlag.value = true
                    _showReSendFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showReSendFailDialogFlag.value = true
                    _showReSendFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    //註冊驗證碼
    fun registerVerifyCodeAction(aEmail: String) {
        viewModelScope.launch {
            val code = "${_verificationCode1.value}${_verificationCode2.value}${_verificationCode3.value}${_verificationCode4.value}"
            if (code.length < 4) {
                _showVerifyCodeInputFailFlag.value = true
                _showVerifyCodeInputFailMsg.value = "VerificationCodeNotEntered"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = accountRepository.registerVerifyCode(aEmail, code)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _verifiedToken.value = result.data.verifiedToken ?: ""
                    _resVerifyCodeSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "registerVerifyCode Res ->${result.message}")
                    _showLoadingView.value = false
                    _showVerifyCodeFailDialogFlag.value = true
                    _showVerifyCodeFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showVerifyCodeFailDialogFlag.value = true
                    _showVerifyCodeFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    //忘記密碼驗證碼
    fun forgotPwVerifyCodeAction(aEmail: String) {
        viewModelScope.launch {
            val code = "${_verificationCode1.value}${_verificationCode2.value}${_verificationCode3.value}${_verificationCode4.value}"
            if (code.length < 4) {
                _showVerifyCodeInputFailFlag.value = true
                _showVerifyCodeInputFailMsg.value = "VerificationCodeNotEntered"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = accountRepository.forgotPwVerifyCode(aEmail, code)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _verifiedToken.value = result.data.verifiedToken ?: ""
                    _resVerifyCodeSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "forgotPwVerifyCode Res ->${result.message}")
                    _showLoadingView.value = false
                    _showVerifyCodeFailDialogFlag.value = true
                    _showVerifyCodeFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showVerifyCodeFailDialogFlag.value = true
                    _showVerifyCodeFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    //忘記密碼驗證碼重發
    fun reSendForgotPwVerificationAction(aEmail: String) {
        viewModelScope.launch {
            _showLoadingView.value = true
            when (val result = accountRepository.forgotPwSendEmail(aEmail)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resReSendSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "reSendForgotPwVerification Res ->${result.message}")
                    _showLoadingView.value = false
                    _showReSendFailDialogFlag.value = true
                    _showReSendFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showReSendFailDialogFlag.value = true
                    _showReSendFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    //取得學生資訊
    fun getStudentInfoAction(aEmail: String, aStudentID: String, aVerifiedToken: String) {
        viewModelScope.launch {
            if (aStudentID.isEmpty()) {
                _showGetStudentInfoFailDialogFlag.value = true
                _showGetStudentInfoFailMsg.value = "StudentIdNotEntered"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = accountRepository.getStudentInfo(aEmail, aStudentID, aVerifiedToken)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _studentIdText.value = result.data.student_id ?: ""
                    _nameText.value = result.data.name ?: ""
                    _phoneText.value = result.data.phone ?: ""
                    _collegeText.value = result.data.college ?: ""
                    _departmentText.value = result.data.department ?: ""
                    _studentTypeText.value = result.data.student_type ?: ""
                    _isStudentVerified.value = true
                    _resGetStudentInfoSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "getStudentInfo Res ->${result.message}")
                    _showLoadingView.value = false
                    _showGetStudentInfoFailDialogFlag.value = true
                    _showGetStudentInfoFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showGetStudentInfoFailDialogFlag.value = true
                    _showGetStudentInfoFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    //註冊送出
    fun registerInfoAction(aEmail: String, aStudentID: String, aPassword: String, aConfirmPassword: String, aVerifiedToken: String) {
        viewModelScope.launch {
            if (aPassword.isEmpty()) {
                _showRegisterInfoInputFailTag.value = 1
                _showRegisterInfoInputFailMsg.value = "PasswordNotEntered"
                return@launch
            }
            if (!aPassword.matches(Regex("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,20}$"))) {
                _showRegisterInfoInputFailTag.value = 1
                _showRegisterInfoInputFailMsg.value = "PasswordInvalidFormat"
                return@launch
            }
            if (aPassword != aConfirmPassword) {
                _showRegisterInfoInputFailTag.value = 2
                _showRegisterInfoInputFailMsg.value = "PasswordMismatch"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = accountRepository.registerInfo(aEmail, aStudentID, aPassword, aVerifiedToken)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resRegisterInfoSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "registerInfo Res ->${result.message}")
                    _showLoadingView.value = false
                    _showRegisterInfoFailDialogFlag.value = true
                    _showRegisterInfoFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showRegisterInfoFailDialogFlag.value = true
                    _showRegisterInfoFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    //重設密碼送出
    fun resetPasswordAction(aEmail: String, aPassword: String, aConfirmPassword: String, aVerifiedToken: String) {
        viewModelScope.launch {
            if (aPassword.isEmpty()) {
                _showResetPasswordInputFailTag.value = 1
                _showResetPasswordInputFailMsg.value = "PasswordNotEntered"
                return@launch
            }
            if (!aPassword.matches(Regex("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,20}$"))) {
                _showResetPasswordInputFailTag.value = 1
                _showResetPasswordInputFailMsg.value = "PasswordInvalidFormat"
                return@launch
            }
            if (aPassword != aConfirmPassword) {
                _showResetPasswordInputFailTag.value = 2
                _showResetPasswordInputFailMsg.value = "PasswordMismatch"
                return@launch
            }
            _showLoadingView.value = true
            when (val result = accountRepository.resetPassword(aEmail, aPassword, aVerifiedToken)) {
                is BaseRepository.Result.Success -> {
                    _showLoadingView.value = false
                    _resResetPasswordSuccessFlag.value = true
                }
                is BaseRepository.Result.Error -> {
                    Log.d("DAE_Develop", "resetPassword Res ->${result.message}")
                    _showLoadingView.value = false
                    _showResetPasswordFailDialogFlag.value = true
                    _showResetPasswordFailMsg.value = result.message
                }
                is BaseRepository.Result.Unauthorized -> {
                    _showLoadingView.value = false
                    _showResetPasswordFailDialogFlag.value = true
                    _showResetPasswordFailMsg.value = "PleaseReLogin"
                }
            }
        }
    }

    //驗證碼重發倒數
    fun startCountdown(seconds: Int) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _remainingTime.value = seconds
            while (_remainingTime.value > 0) {
                delay(1000)
                _remainingTime.value -= 1
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
    }

    fun updateInputEmail(value: String) {
        _emailText.value = value
    }

    fun updateInputVerificationCode1(value: String) {
        _verificationCode1.value = value
    }

    fun updateInputVerificationCode2(value: String) {
        _verificationCode2.value = value
    }

    fun updateInputVerificationCode3(value: String) {
        _verificationCode3.value = value
    }

    fun updateInputVerificationCode4(value: String) {
        _verificationCode4.value = value
    }

    fun updateInputStudentId(value: String) {
        _studentIdText.value = value
        // 學號變動 → 重置驗證狀態並清空已帶入的學生資料
        _isStudentVerified.value = false
        _nameText.value = ""
        _phoneText.value = ""
        _collegeText.value = ""
        _departmentText.value = ""
        _studentTypeText.value = ""
    }

    //點下一步但尚未驗證學號
    fun showStudentNotVerifiedAlert() {
        _showGetStudentInfoFailDialogFlag.value = true
        _showGetStudentInfoFailMsg.value = "StudentNotVerified"
    }

    fun updateInputName(value: String) {
        _nameText.value = value
    }

    fun updateInputPhone(value: String) {
        _phoneText.value = value
    }

    fun updateInputCollege(value: String) {
        _collegeText.value = value
    }

    fun updateInputDepartment(value: String) {
        _departmentText.value = value
    }

    fun updateInputStudentType(value: String) {
        _studentTypeText.value = value
    }

    fun updateInputPassword(value: String) {
        _passwordText.value = value
    }

    fun updateInputConfirmPassword(value: String) {
        _confirmPasswordText.value = value
    }

    fun resetResRegisterSendEmailSuccessFlag(value: Boolean) {
        _resRegisterSendEmailSuccessFlag.value = value
    }

    fun resetShowRegisterSendEmailFailDialogFlag(value: Boolean) {
        _showRegisterSendEmailFailDialogFlag.value = value
    }

    fun resetShowRegisterEmailInputFailFlag(value: Boolean) {
        _showRegisterEmailInputFailFlag.value = value
    }

    fun resetResForgotPwSendEmailSuccessFlag(value: Boolean) {
        _resForgotPwSendEmailSuccessFlag.value = value
    }

    fun resetShowForgotPwSendEmailFailDialogFlag(value: Boolean) {
        _showForgotPwSendEmailFailDialogFlag.value = value
    }

    fun resetShowForgotPwEmailInputFailFlag(value: Boolean) {
        _showForgotPwEmailInputFailFlag.value = value
    }

    fun resetResVerifyCodeSuccessFlag(value: Boolean) {
        _resVerifyCodeSuccessFlag.value = value
    }

    fun resetShowVerifyCodeFailDialogFlag(value: Boolean) {
        _showVerifyCodeFailDialogFlag.value = value
    }

    fun resetShowVerifyCodeInputFailFlag(value: Boolean) {
        _showVerifyCodeInputFailFlag.value = value
    }

    fun resetResReSendSuccessFlag(value: Boolean) {
        _resReSendSuccessFlag.value = value
    }

    fun resetShowReSendFailDialogFlag(value: Boolean) {
        _showReSendFailDialogFlag.value = value
    }

    fun resetResGetStudentInfoSuccessFlag(value: Boolean) {
        _resGetStudentInfoSuccessFlag.value = value
    }

    fun resetShowGetStudentInfoFailDialogFlag(value: Boolean) {
        _showGetStudentInfoFailDialogFlag.value = value
    }

    fun resetResRegisterInfoSuccessFlag(value: Boolean) {
        _resRegisterInfoSuccessFlag.value = value
    }

    fun resetShowRegisterInfoFailDialogFlag(value: Boolean) {
        _showRegisterInfoFailDialogFlag.value = value
    }

    fun resetShowRegisterInfoInputFailTag(value: Int) {
        _showRegisterInfoInputFailTag.value = value
    }

    fun resetResResetPasswordSuccessFlag(value: Boolean) {
        _resResetPasswordSuccessFlag.value = value
    }

    fun resetShowResetPasswordFailDialogFlag(value: Boolean) {
        _showResetPasswordFailDialogFlag.value = value
    }

    fun resetShowResetPasswordInputFailTag(value: Int) {
        _showResetPasswordInputFailTag.value = value
    }
}
