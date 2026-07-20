package com.dae.stems_campus.ui.components


import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.dae.stems_campus.R

class BiometricHelper(private val activity: FragmentActivity) {

    // minSdk 已提升到 30，可安全組合臉部(WEAK) + 裝置密碼(DEVICE_CREDENTIAL)。
    // 系統辨識畫面會自帶「使用密碼」直接走手機解鎖 PIN，不需自訂 fallback。
    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

    /** 回傳 BiometricManager.BIOMETRIC_* 原始碼，讓呼叫端分辨「沒硬體」和「有硬體但沒註冊」 */
    fun canAuthenticate(): Int =
        BiometricManager.from(activity).canAuthenticate(authenticators)

    fun authenticate(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        // 含 DEVICE_CREDENTIAL 時不可設 setNegativeButtonText，密碼入口由系統畫面自帶
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_prompt_title))
            .setSubtitle(activity.getString(R.string.use_face_or_fingerprint_verification))
            .setAllowedAuthenticators(authenticators)
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
                    // 使用者主動取消（返回鍵、下滑關閉）→ 什麼都不做，其餘才報錯
                    if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
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
