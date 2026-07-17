package com.dae.stems_campus.ui.components


import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.dae.stems_campus.R

class BiometricHelper(private val activity: FragmentActivity) {

    // 不含 DEVICE_CREDENTIAL：鎖屏 PIN 只證明「解得開這台手機」，不足以當帳號層級的本人驗證。
    // 另外 BIOMETRIC_WEAK or DEVICE_CREDENTIAL 在 API 29 是非法組合，minSdk 29 不能用。
    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK

    /** 回傳 BiometricManager.BIOMETRIC_* 原始碼，讓呼叫端分辨「沒硬體」和「有硬體但沒註冊」 */
    fun canAuthenticate(): Int =
        BiometricManager.from(activity).canAuthenticate(authenticators)

    fun authenticate(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_prompt_title))
            .setSubtitle(activity.getString(R.string.use_face_or_fingerprint_verification))
            .setAllowedAuthenticators(authenticators)
            .setNegativeButtonText(activity.getString(R.string.biometric_prompt_use_password))
            .setConfirmationRequired(false)
            .build()

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    // 使用者主動取消或按「使用密碼」不是錯誤，直接收掉讓他回原畫面輸密碼
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_CANCELED
                    ) return
                    onError(errString.toString())
                }

                // 單次比對不符而已，prompt 還開著可以再試 → 不能在這裡結束流程
                override fun onAuthenticationFailed() = Unit
            })

        biometricPrompt.authenticate(promptInfo)
    }
}
