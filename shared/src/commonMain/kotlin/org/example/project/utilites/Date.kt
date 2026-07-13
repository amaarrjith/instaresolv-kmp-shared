package org.example.project.utilities
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

fun formatTimestamp(
    timestamp: Long?,
    outputFormat: String = "dd MMM yyyy"
): String {
    timestamp ?: return ""

    val dateTime = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val day = dateTime.day.toString().padStart(2, '0')
    val monthNumber = dateTime.month.number.toString().padStart(2, '0')
    val month = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )[dateTime.month.number - 1]

    return when (outputFormat) {
        "dd MMM yyyy" -> "$day $month ${dateTime.year}"
        "yyyy-MM-dd" -> "${dateTime.year}-$monthNumber-$day"
        "dd/MM/yyyy" -> "$day/$monthNumber/${dateTime.year}"
        "dd-MM-yyyy" -> "$day-$monthNumber-${dateTime.year}"
        else -> "$day $month ${dateTime.year}"
    }
}

fun convertTo24HourFormat(
    time: String?,
    convertToUtc: Boolean = false
): String {
    if (time.isNullOrBlank()) return ""

    val regex = Regex("""(\d{1,2})\s*:\s*(\d{2})\s*(AM|PM)""", RegexOption.IGNORE_CASE)
    val match = regex.matchEntire(time.trim()) ?: return ""

    var (hour, minute, period) = match.destructured
    var hourInt = hour.toInt()

    if (period.equals("AM", ignoreCase = true)) {
        if (hourInt == 12) hourInt = 0
    } else {
        if (hourInt != 12) hourInt += 12
    }

    if (!convertToUtc) {
        return "${hourInt.toString().padStart(2, '0')}:${minute.padStart(2, '0')}:00"
    }

    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date

    val localDateTime = LocalDateTime(
        year = today.year,
        month = today.month.number,
        day = today.day,
        hour = hourInt,
        minute = minute.toInt(),
        second = 0
    )

    val utcDateTime = localDateTime
        .toInstant(TimeZone.currentSystemDefault())
        .toLocalDateTime(TimeZone.UTC)

    return buildString {
        append(utcDateTime.hour.toString().padStart(2, '0'))
        append(":")
        append(utcDateTime.minute.toString().padStart(2, '0'))
        append(":")
        append(utcDateTime.second.toString().padStart(2, '0'))
    }
}