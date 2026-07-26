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

/**
 * Parses [input] using [inputPattern] and re-formats it as [outputPattern].
 * Supports: yyyy, MMMM (full month name), MMM (short month name),
 * MM (month number), dd, HH, mm, ss.
 */
fun formatDate(input: String, inputPattern: String, outputPattern: String): String {
    val needsTime = inputPattern.hasTimeTokens() || outputPattern.hasTimeTokens()

    return try {
        val instant = Instant.parse(input)
        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = instant.toLocalDateTime(timeZone)
        if (needsTime) {
            localDateTime.format(buildDateTimeFormat(outputPattern))
        } else {
            localDateTime.date.format(buildDateFormat(outputPattern))
        }
    } catch (e: Exception) {
        if (needsTime) {
            val dateTime = LocalDateTime.parse(input, buildDateTimeFormat(inputPattern))
            dateTime.format(buildDateTimeFormat(outputPattern))
        } else {
            val date = LocalDate.parse(input, buildDateFormat(inputPattern))
            date.format(buildDateFormat(outputPattern))
        }
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

    val systemTimeZone = TimeZone.currentSystemDefault()

    val past = try {
        // For ISO-8601 strings like "2026-07-10T12:30:00Z"
        Instant.parse(input)
    } catch (_: Exception) {
        val dateTime = LocalDateTime.parse(
            input,
            buildDateTimeFormat(inputPattern)
        )

        dateTime.toInstant(
            if (isUtc) TimeZone.UTC else systemTimeZone
        )
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

fun utcToLocal(
    date: String,
    inputFormat: String,
    outputFormat: String
): String {
    return try {
        when {
            inputFormat == "HH:mm:ss" && outputFormat == "HH:mm" -> {

                val input = LocalTime.parse(
                    date,
                    LocalTime.Format {
                        hour()
                        char(':')
                        minute()
                        char(':')
                        second()
                    }
                )

                // Use today's UTC date
                val today = Clock.System.now()
                    .toLocalDateTime(TimeZone.UTC)
                    .date

                // Create UTC LocalDateTime
                val utcDateTime = LocalDateTime(today, input)

                // Convert UTC -> Local timezone
                val instant = utcDateTime.toInstant(TimeZone.UTC)
                val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

                LocalTime.Format {
                    hour()
                    char(':')
                    minute()
                }.format(localDateTime.time)
            }

            inputFormat == "HH:mm:ss" && outputFormat == "hh:mm a" -> {

                val input = LocalTime.parse(
                    date,
                    LocalTime.Format {
                        hour()
                        char(':')
                        minute()
                        char(':')
                        second()
                    }
                )

                val today = Clock.System.now()
                    .toLocalDateTime(TimeZone.UTC)
                    .date

                val utcDateTime = LocalDateTime(today, input)
                val instant = utcDateTime.toInstant(TimeZone.UTC)
                val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

                LocalTime.Format {
                    amPmHour()
                    char(':')
                    minute()
                    char(' ')
                    amPmMarker("AM", "PM")
                }.format(localDateTime.time)
            }

            inputFormat == "yyyy-MM-dd HH:mm:ss" &&
                    outputFormat == "dd-MM-yyyy HH:mm" -> {

                val localDateTime = LocalDateTime.parse(
                    date,
                    LocalDateTime.Format {
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
                )

                val instant = localDateTime.toInstant(TimeZone.UTC)
                val converted = instant.toLocalDateTime(TimeZone.currentSystemDefault())

                LocalDateTime.Format {
                    dayOfMonth()
                    char('-')
                    monthNumber()
                    char('-')
                    year()
                    char(' ')
                    hour()
                    char(':')
                    minute()
                }.format(converted)
            }

            else -> date
        }
    } catch (e: Exception) {
        date
    }
}