package com.dae.stems_campus.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun Double.toAmountString(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.ENGLISH).apply {
        isGroupingUsed = true
    } as DecimalFormat
    formatter.minimumFractionDigits = 0
    formatter.maximumFractionDigits = 0
    formatter.roundingMode = RoundingMode.HALF_UP
    return formatter.format(this)
}

/**
 * 顯示到小數點兩位，超過則無條件捨去（truncate）。
 * 0 時直接回傳 "0"。
 */
fun Double.toTwoDecimalString(): String {
    if (this == 0.0) return "0"
    val formatter = DecimalFormat("0.00").apply {
        roundingMode = RoundingMode.DOWN
    }
    return formatter.format(this)
}