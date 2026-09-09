package com.dae.stems_campus.utils

import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * 把後端 ISO 8601（含 Z 或時區）字串轉成裝置本地時間，依 pattern 格式化
 * 失敗回傳空字串
 */
fun String.toLocalDateTimeText(pattern: String = "yyyy-MM-dd HH:mm"): String {
    if (isEmpty()) return ""
    return try {
        OffsetDateTime.parse(this)
            .atZoneSameInstant(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(pattern))
    } catch (e: Exception) {
        ""
    }
}

fun String.elapsedTime(): String {
    return try {
        val startDate = ZonedDateTime.parse(this).toInstant()
        val elapsed = ChronoUnit.SECONDS.between(startDate, Instant.now())

        if (elapsed < 0) return "00:00:00"

        val hours = elapsed / 3600
        val minutes = (elapsed % 3600) / 60
        val seconds = elapsed % 60

        String.format("%02d:%02d:%02d", hours, minutes, seconds)

    } catch (e: Exception) {
        "00:00:00"
    }
}

fun computeDuration(startDate: String, endDate: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val startTime = OffsetDateTime.parse(startDate, formatter)
        val endTime = OffsetDateTime.parse(endDate, formatter)
        val duration = Duration.between(startTime, endTime)
        val hours = duration.toHours()
        val minutes = duration.toMinutes() % 60
        when {
            hours == 0L -> "${minutes}分鐘"
            minutes == 0L -> "${hours}小時"
            else -> "${hours}小時${minutes}分鐘"
        }
    } catch (e: Exception) {
        ""
    }
}

/**
 * 計算兩個時間的間隔，**只要有餘秒就無條件進位成一分鐘**。
 * 例：0分40秒 → 1分鐘、1分30秒 → 2分鐘、1小時0分1秒 → 1小時1分鐘。
 * 用意是不讓使用者看到「0分鐘」這種像是沒用到電的顯示。
 * 結束時間早於開始時間（資料異常）一律當 0 分鐘，不顯示負值。
 */
fun computeDurationAtLeastOneMinute(startDate: String, endDate: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val startTime = OffsetDateTime.parse(startDate, formatter)
        val endTime = OffsetDateTime.parse(endDate, formatter)
        val duration = Duration.between(startTime, endTime)
        val totalSeconds = duration.seconds
        // 無條件進位：(秒 + 59) / 60
        val adjustedMinutes = if (totalSeconds <= 0L) 0L else (totalSeconds + 59) / 60
        val hours = adjustedMinutes / 60
        val minutes = adjustedMinutes % 60
        when {
            hours == 0L -> "${minutes}分鐘"
            minutes == 0L -> "${hours}小時"
            else -> "${hours}小時${minutes}分鐘"
        }
    } catch (e: Exception) {
        "--"
    }
}