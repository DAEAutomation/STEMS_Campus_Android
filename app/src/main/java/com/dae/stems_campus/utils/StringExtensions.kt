package com.dae.stems_campus.utils

import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

fun computeDurationAtLeastOneMinute(startDate: String, endDate: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val startTime = OffsetDateTime.parse(startDate, formatter)
        val endTime = OffsetDateTime.parse(endDate, formatter)
        val duration = Duration.between(startTime, endTime)
        val totalMinutes = duration.toMinutes()
        val adjustedMinutes = if (totalMinutes == 0L && duration.seconds > 0) 1L else totalMinutes
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