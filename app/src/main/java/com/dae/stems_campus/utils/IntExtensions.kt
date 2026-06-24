package com.dae.stems_campus.utils

import java.text.NumberFormat
import java.util.Locale

/** 將數字格式化為帶千位符號的字串，例如 1234567 -> "1,234,567" */
fun Int.toThousandsSeparator(): String =
    NumberFormat.getNumberInstance(Locale.US).format(this)

fun Int.calculateDuration(): String {
    val hours = this / 60
    val minutes = kotlin.math.abs(this % 60)

    return when {
        hours == 0 -> "$minutes 分鐘"
        minutes == 0 -> "$hours 小時"
        else -> "$hours 小時 $minutes 分鐘"
    }
}