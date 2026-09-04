package com.dae.stems_campus.network

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseUrlHolder @Inject constructor() {
    @Volatile
    var baseUrl: String? = null
        set(value) {
            field = value
            // 多校環境，crash 報告要能分辨是哪一所學校的後端
            FirebaseCrashlytics.getInstance().setCustomKey("baseUrl", value.orEmpty())
        }
}
