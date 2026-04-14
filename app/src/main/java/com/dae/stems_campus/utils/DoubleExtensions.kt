package com.dae.stems_campus.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun Double.toAmountString(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.ENGLISH).apply {
        isGroupingUsed = true
    }

    return when {
        this >= 1 -> {
            // 大於等於 1：四捨五入到整數，帶千分位
            (formatter as DecimalFormat).apply {
                minimumFractionDigits = 0
                maximumFractionDigits = 0
            }
            formatter.format(this)
        }
        this > 0 -> {
            // 大於 0 但小於 1：顯示小數點後兩位
            (formatter as DecimalFormat).apply {
                minimumFractionDigits = 0
                maximumFractionDigits = 2
            }
            formatter.format(this)
        }
        else -> "0"
    }
}