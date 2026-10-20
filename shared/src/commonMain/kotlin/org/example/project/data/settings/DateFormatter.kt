package org.example.project.data.settings

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import androidx.compose.runtime.Composable
import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val dateFormatCache = mutableMapOf<String, DateTimeFormat<LocalDate>>()
private val dateTimeFormatCache = mutableMapOf<String, DateTimeFormat<LocalDateTime>>()

private fun getCachedDateFormat(pattern: String): DateTimeFormat<LocalDate> {
    return dateFormatCache[pattern] ?: buildDateFormat(pattern).also { dateFormatCache[pattern] = it }
}

private fun getCachedDateTimeFormat(pattern: String): DateTimeFormat<LocalDateTime> {
    return dateTimeFormatCache[pattern] ?: buildDateTimeFormat(pattern).also { dateTimeFormatCache[pattern] = it }
}

/**
 * Parses [input] using [inputPattern] and re-formats it as [outputPattern].
 * Supports: yyyy, MMMM (full month name), MMM (short month name),
 * MM (month number), dd, HH, mm, ss.
 */
fun formatDate(input: String, inputPattern: String, outputPattern: String): String {
    if (input.isBlank()) return input
    val needsTime = inputPattern.hasTimeTokens() || outputPattern.hasTimeTokens()

    val isIsoInstant = input.contains("T") && (input.endsWith("Z") || input.contains("+"))
    if (isIsoInstant) {
        try {
            val instant = Instant.parse(input)
            val timeZone = TimeZone.currentSystemDefault()
            val localDateTime = instant.toLocalDateTime(timeZone)
            return if (needsTime) {
                localDateTime.format(getCachedDateTimeFormat(outputPattern))
            } else {
                localDateTime.date.format(getCachedDateFormat(outputPattern))
            }
        } catch (_: Exception) {
            // fallback below
        }
    }

    return try {
        if (needsTime) {
            val dateTime = LocalDateTime.parse(input, getCachedDateTimeFormat(inputPattern))
            dateTime.format(getCachedDateTimeFormat(outputPattern))
        } else {
            val date = LocalDate.parse(input, getCachedDateFormat(inputPattern))
            date.format(getCachedDateFormat(outputPattern))
        }
    } catch (e: Exception) {
        input
    }
}

private fun String.hasTimeTokens() = contains("HH") || contains("mm") || contains("ss")

private fun buildDateFormat(pattern: String): DateTimeFormat<LocalDate> = LocalDate.Format {
    var i = 0
    while (i < pattern.length) {
        when {
            pattern.startsWith("yyyy", i) -> { year(); i += 4 }
            pattern.startsWith("MMMM", i) -> { monthName(MonthNames.ENGLISH_FULL); i += 4 }
            pattern.startsWith("MMM", i) -> { monthName(MonthNames.ENGLISH_ABBREVIATED); i += 3 }
            pattern.startsWith("MM", i) -> { monthNumber(); i += 2 }
            pattern.startsWith("dd", i) -> { day(); i += 2 }
            else -> { char(pattern[i]); i += 1 }
        }
    }
}

private fun buildDateTimeFormat(pattern: String): DateTimeFormat<LocalDateTime> = LocalDateTime.Format {
    var i = 0
    while (i < pattern.length) {
        when {
            pattern.startsWith("yyyy", i) -> { year(); i += 4 }
            pattern.startsWith("MMMM", i) -> { monthName(MonthNames.ENGLISH_FULL); i += 4 }
            pattern.startsWith("MMM", i) -> { monthName(MonthNames.ENGLISH_ABBREVIATED); i += 3 }
            pattern.startsWith("MM", i) -> { monthNumber(); i += 2 }
            pattern.startsWith("dd", i) -> { day(); i += 2 }
            pattern.startsWith("HH", i) -> { hour(); i += 2 }
            pattern.startsWith("mm", i) -> { minute(); i += 2 }
            pattern.startsWith("ss", i) -> { second(); i += 2 }
            else -> { char(pattern[i]); i += 1 }
        }
    }
}

@Composable
fun timeAgo(
    input: String,
    inputPattern: String = "yyyy-MM-dd HH:mm:ss",
    isUtc: Boolean = false
): String {
    if (input.isBlank()) return ""

    val systemTimeZone = TimeZone.currentSystemDefault()

    val isIso = input.contains("T") && (input.endsWith("Z") || input.contains("+"))
    val past = if (isIso) {
        try {
            Instant.parse(input)
        } catch (_: Exception) {
            try {
                val dateTime = LocalDateTime.parse(input, getCachedDateTimeFormat(inputPattern))
                dateTime.toInstant(if (isUtc) TimeZone.UTC else systemTimeZone)
            } catch (_: Exception) {
                return ""
            }
        }
    } else {
        try {
            val dateTime = LocalDateTime.parse(input, getCachedDateTimeFormat(inputPattern))
            dateTime.toInstant(if (isUtc) TimeZone.UTC else systemTimeZone)
        } catch (_: Exception) {
            try {
                Instant.parse(input)
            } catch (_: Exception) {
                return ""
            }
        }
    }

    val now = Clock.System.now()

    if (past > now) return stringResource(Res.string.just_now)

    val period = past.periodUntil(now, systemTimeZone)

    return when {
        period.years > 0 -> pluralizeTimeAgo(period.years, Res.string.time_ago_year, Res.string.time_ago_years)
        period.months > 0 -> pluralizeTimeAgo(period.months, Res.string.time_ago_month, Res.string.time_ago_months)
        period.days >= 7 -> pluralizeTimeAgo(period.days / 7, Res.string.time_ago_week, Res.string.time_ago_weeks)
        period.days > 0 -> pluralizeTimeAgo(period.days, Res.string.time_ago_day, Res.string.time_ago_days)
        period.hours > 0 -> pluralizeTimeAgo(period.hours, Res.string.time_ago_hour, Res.string.time_ago_hours)
        period.minutes > 0 -> pluralizeTimeAgo(period.minutes, Res.string.time_ago_minute, Res.string.time_ago_minutes)
        period.seconds > 0 -> pluralizeTimeAgo(period.seconds, Res.string.time_ago_second, Res.string.time_ago_seconds)
        else -> stringResource(Res.string.just_now)
    }
}

@Composable
private fun pluralizeTimeAgo(value: Int, singleRes: StringResource, pluralRes: StringResource): String {
    return if (value == 1) {
        stringResource(singleRes)
    } else {
        stringResource(pluralRes, value)
    }
}

private val timeFormatWithSeconds = LocalTime.Format {
    hour()
    char(':')
    minute()
    char(':')
    second()
}

private val timeFormatWithoutSeconds = LocalTime.Format {
    hour()
    char(':')
    minute()
}

private val time12HourFormat = LocalTime.Format {
    amPmHour()
    char(':')
    minute()
    char(' ')
    amPmMarker("AM", "PM")
}

private val dateTimeStandardInputFormat = LocalDateTime.Format {
    year()
    char('-')
    monthNumber()
    char('-')
    dayOfMonth()
    char(' ')
    hour()
    char(':')
    minute()
    char(':')
    second()
}

private val dateTimeStandardOutputFormat = LocalDateTime.Format {
    dayOfMonth()
    char('-')
    monthNumber()
    char('-')
    year()
    char(' ')
    hour()
    char(':')
    minute()
}

fun utcToLocal(
    date: String,
    inputFormat: String,
    outputFormat: String
): String {
    if (date.isBlank()) return date
    return try {
        when {
            inputFormat == "HH:mm:ss" && outputFormat == "HH:mm" -> {
                val input = LocalTime.parse(date, timeFormatWithSeconds)
                val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
                val utcDateTime = LocalDateTime(today, input)
                val instant = utcDateTime.toInstant(TimeZone.UTC)
                val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                timeFormatWithoutSeconds.format(localDateTime.time)
            }

            inputFormat == "HH:mm:ss" && outputFormat == "hh:mm a" -> {
                val input = LocalTime.parse(date, timeFormatWithSeconds)
                val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
                val utcDateTime = LocalDateTime(today, input)
                val instant = utcDateTime.toInstant(TimeZone.UTC)
                val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                time12HourFormat.format(localDateTime.time)
            }

            inputFormat == "yyyy-MM-dd HH:mm:ss" && outputFormat == "dd-MM-yyyy HH:mm" -> {
                val localDateTime = LocalDateTime.parse(date, dateTimeStandardInputFormat)
                val instant = localDateTime.toInstant(TimeZone.UTC)
                val converted = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                dateTimeStandardOutputFormat.format(converted)
            }

            else -> date
        }
    } catch (e: Exception) {
        date
    }
}

fun parseTimeToMinutes(timeStr: String): Int {
    if (timeStr.isBlank()) return 0
    val parts = timeStr.trim().split(Regex("[:\\s]+"))
    if (parts.size >= 3 && (parts.last().equals("AM", ignoreCase = true) || parts.last().equals("PM", ignoreCase = true))) {
        val h = parts[0].toIntOrNull() ?: 0
        val m = parts[1].toIntOrNull() ?: 0
        val ampm = parts.last().uppercase()
        var hour24 = h
        if (ampm == "PM" && h < 12) hour24 += 12
        if (ampm == "AM" && h == 12) hour24 = 0
        return hour24 * 60 + m
    } else if (parts.size >= 2) {
        val h = parts[0].toIntOrNull() ?: 0
        val m = parts[1].toIntOrNull() ?: 0
        return h * 60 + m
    }
    return 0
}