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