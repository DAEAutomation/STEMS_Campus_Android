package com.dae.stems_campus.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.dae.stems_campus.ui.components.PreferencesStrings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// 定義 DataStore 名稱
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

open class UserPreferencesRepository @Inject constructor (@ApplicationContext private val context: Context){

    // 讀取 API base URL
    val getApiDomainValue: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.API_DOMAIN] ?: ""
    }

    // 儲存 API base URL
    suspend fun setApiDomainValue(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.API_DOMAIN] = value
        }
    }

    // 讀取 Name
    var getNameValue: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.NAME] ?: ""
    }

    // 儲存 Name
    suspend fun setNameValue(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.NAME] = value
        }
    }

    // 讀取 UUID
    var getUUIDValue: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.UUID] ?: ""
    }

    // 儲存 UUID
    suspend fun setUUIDValue(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.UUID] = value
        }
    }

    // 讀取 mqtt topic
    var getMqttTopicValue: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.MQTT_TOPIC] ?: ""
    }

    // 儲存 mqtt topic
    suspend fun setMqttTopicValue(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.MQTT_TOPIC] = value
        }
    }

    // 讀取 mqtt host
    var getMqttHostValue: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.MQTT_HOST] ?: ""
    }

    // 儲存 mqtt host
    suspend fun setMqttHostValue(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.MQTT_HOST] = value
        }
    }

    // 讀取 mqtt port
    var getMqttPortValue: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.MQTT_PORT] ?: 0
    }

    // 儲存 mqtt port
    suspend fun setMqttPortValue(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.MQTT_PORT] = value
        }
    }

    // 讀取 Deposit mac address
    var getDepositMacAddressValue: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.DEPOSIT_MAC_ADDRESS] ?: ""
    }

    // 儲存 Deposit mac address
    suspend fun setDepositMacAddressValue(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.DEPOSIT_MAC_ADDRESS] = value
        }
    }

    // 讀取 Subscription topic
    var getSubscriptionTopicValue: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.SUBSCRIPTION_TOPIC] ?: ""
    }

    // 儲存 Subscription topic
    suspend fun setSubscriptionTopiValue(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.SUBSCRIPTION_TOPIC] = value
        }
    }

    // 讀取 Account & Password Checkbox 狀態
    val getRememberLoginInfoCheckedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.REMEMBER_LOGIN_INFO_CHECKBOX] ?: false // 默認值為 false
    }

    // 儲存 Account & Password Checkbox 狀態
    suspend fun setRememberLoginInfoChecked(isChecked: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.REMEMBER_LOGIN_INFO_CHECKBOX] = isChecked
        }
    }

    // 讀取 Biometric enabled 狀態
    val getBiometricValue: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesStrings.BIOMETRIC_ENABLED] ?: false // 默認值為 false
    }

    // 儲存 Biometric enabled 狀態
    suspend fun setBiometricValue(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesStrings.BIOMETRIC_ENABLED] = value
        }
    }

    // 讀取 Language
//    val getLanguageValue: Flow<String> = context.dataStore.data.map { preferences ->
//        preferences[PreferencesStrings.LANGUAGE_VALUE] ?: "zh"
//    }
//
//    // 儲存 Language
//    suspend fun setLanguageValue(value: String) {
//        context.dataStore.edit { preferences ->
//            preferences[PreferencesStrings.LANGUAGE_VALUE] = value
//        }
//    }




}