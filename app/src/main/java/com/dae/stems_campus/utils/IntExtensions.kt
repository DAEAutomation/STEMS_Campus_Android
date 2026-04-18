package com.dae.stems_campus.utils

fun Int.calculateDuration(): String {
    val hours = this / 60
    val minutes = kotlin.math.abs(this % 60)

    return when {
        hours == 0 -> "$minutes 分鐘"
        minutes == 0 -> "$hours 小時"
        else -> "$hours 小時 $minutes 分鐘"
    }
}