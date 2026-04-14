package com.dae.stems_campus.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

open class CredentialRepository@Inject constructor (@ApplicationContext private val context: Context) {

    private val sharedPrefs by lazy {
        initializeEncryptedPrefs()
    }

    private fun initializeEncryptedPrefs(): SharedPreferences {
        return try {
            createEncryptedPrefs()
        } catch (e: Exception) {
            Log.e("DAE_Develop", "第一次初始化失敗: ${e.message}", e)
            handleEncryptionFailure()
        }
    }

    private fun createEncryptedPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun handleEncryptionFailure(): SharedPreferences {
        Log.w("CredentialRepository", "開始清除損壞的加密資料")

        // 1. 刪除 SharedPreferences 檔案
        try {
            val prefsFile = File("${context.filesDir.parent}/shared_prefs/secure_prefs.xml")
            if (prefsFile.exists()) {
                val deleted = prefsFile.delete()
                Log.d("CredentialRepository", "刪除 secure_prefs.xml: $deleted")
            }
        } catch (e: Exception) {
            Log.e("CredentialRepository", "刪除 prefs 檔案失敗", e)
        }

        // 2. 刪除 master key keyset 檔案 (重要!)
        try {
            val keysetFile = File("${context.filesDir.parent}/shared_prefs/__androidx_security_crypto_encrypted_prefs_key_keyset__.xml")
            if (keysetFile.exists()) {
                val deleted = keysetFile.delete()
                Log.d("CredentialRepository", "刪除 keyset 檔案: $deleted")
            }
        } catch (e: Exception) {
            Log.e("CredentialRepository", "刪除 keyset 檔案失敗", e)
        }

        // 3. 刪除 Android Keystore 中的 master key
        try {
            val keyStore = java.security.KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            if (keyStore.containsAlias("_androidx_security_master_key_")) {
                keyStore.deleteEntry("_androidx_security_master_key_")
                Log.d("CredentialRepository", "成功刪除 AndroidKeyStore 中的 master key")
            } else {
                Log.d("CredentialRepository", "AndroidKeyStore 中沒有 master key")
            }
        } catch (e: Exception) {
            Log.e("CredentialRepository", "刪除 AndroidKeyStore key 失敗", e)
        }

        // 4. 重新建立 (包裹在 try-catch 中)
        return try {
            Log.d("CredentialRepository", "嘗試重新建立 EncryptedSharedPreferences")
            createEncryptedPrefs()
        } catch (e: Exception) {
            Log.e("CredentialRepository", "重新建立仍然失敗,使用普通 SharedPreferences", e)
            // 最後手段:使用普通 SharedPreferences
            context.getSharedPreferences("secure_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    open fun saveCredentials(username: String, password: String) {
        sharedPrefs.edit()
            .putString("username", username)
            .putString("password", password)
            .apply()
    }

    open fun saveTokenCredentials(accessToken: String, refreshToken: String) {
        sharedPrefs.edit()
            .putString("accessToken", accessToken)
            .putString("refreshToken", refreshToken)
            .apply()
    }

    fun saveAccessTokenCredentials(accessToken: String) {
        sharedPrefs.edit()
            .putString("accessToken", accessToken)
            .apply()
    }

    open fun saveMqttCredentials(username: String, password: String) {
        sharedPrefs.edit()
            .putString("mqttUsername", username)
            .putString("mqttPassword", password)
            .apply()
    }

    open fun getUsername(): String? = sharedPrefs.getString("username", null)
    open fun getPassword(): String? = sharedPrefs.getString("password", null)
    open fun getAccessToken(): String? = sharedPrefs.getString("accessToken", null)
    open fun getRefreshToken(): String? = sharedPrefs.getString("refreshToken", null)
    open fun getMqttUsername(): String? = sharedPrefs.getString("mqttUsername", null)
    open fun getMqttPassword(): String? = sharedPrefs.getString("mqttPassword", null)

    fun deleteToken() {
        sharedPrefs.edit().remove("accessToken").apply()
        sharedPrefs.edit().remove("refreshToken").apply()
    }

    fun clearCredentials() {
        sharedPrefs.edit().clear().apply()
    }
}