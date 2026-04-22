package com.dae.stems_campus.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseUrlHolder @Inject constructor() {
    @Volatile
    var baseUrl: String? = null
}
