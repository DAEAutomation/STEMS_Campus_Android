package com.dae.stems_campus.utils

/**
 * 比較語意化版本號（如 "1.0.0" vs "1.1.0"）
 * @return true 表示 currentVersion 比 requiredVersion 舊（需要更新）
 */
fun isVersionOutdated(currentVersion: String, requiredVersion: String): Boolean {
    val cur = currentVersion.split(".").map { it.toIntOrNull() ?: 0 }
    val req = requiredVersion.split(".").map { it.toIntOrNull() ?: 0 }
    val maxLen = maxOf(cur.size, req.size)
    for (i in 0 until maxLen) {
        val c = cur.getOrNull(i) ?: 0
        val r = req.getOrNull(i) ?: 0
        if (c < r) return true
        if (c > r) return false
    }
    return false
}
