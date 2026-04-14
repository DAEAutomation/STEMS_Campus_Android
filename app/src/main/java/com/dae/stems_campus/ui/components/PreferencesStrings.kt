package com.dae.stems_campus.ui.components
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class PreferencesStrings {
    companion object {
        val API_DOMAIN = stringPreferencesKey("C1")
        val NAME = stringPreferencesKey("C2")
        val UUID= stringPreferencesKey("C3")
        val ROLE = stringPreferencesKey("C4")
        val MQTT_TOPIC = stringPreferencesKey("C5")
        val MQTT_HOST = stringPreferencesKey("C6")
        val MQTT_PORT = intPreferencesKey("C7")
        val SUBSCRIPTION_TOPIC = stringPreferencesKey("C8")
        val REMEMBER_LOGIN_INFO_CHECKBOX = booleanPreferencesKey("C9")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("C10")
        val DEPOSIT_MAC_ADDRESS = stringPreferencesKey("C11")
        val REFUND_ID = intPreferencesKey("C12")









    }
}