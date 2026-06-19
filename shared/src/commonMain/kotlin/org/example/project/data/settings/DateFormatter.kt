package org.example.project.data.settings

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant

/**
 * Parses [input] using [inputPattern] and re-formats it as [outputPattern].
 * Supports: yyyy, MMMM (full month name), MMM (short month name),
 * MM (month number), dd, HH, mm, ss.
 */
fun formatDate(input: String, inputPattern: String, outputPattern: String): String {
    val needsTime = inputPattern.hasTimeTokens() || outputPattern.hasTimeTokens()

    return if (needsTime) {
        val dateTime = LocalDateTime.parse(input, buildDateTimeFormat(inputPattern))
        dateTime.format(buildDateTimeFormat(outputPattern))
    } else {
        val date = LocalDate.parse(input, buildDateFormat(inputPattern))
        date.format(buildDateFormat(outputPattern))
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
fun timeAgo(
    input: String,
    inputPattern: String = "yyyy-MM-dd HH:mm:ss"
): String {

    val dateTime = LocalDateTime.parse(input, buildDateTimeFormat(inputPattern))
    val timeZone = TimeZone.currentSystemDefault()

    val past = dateTime.toInstant(timeZone)
    val now = kotlin.time.Clock.System.now()// or Clock.System.now() for newer versions

    if (past > now) return "Just now"

    val period = past.periodUntil(now, timeZone)

    return when {
        period.years > 0 ->
            pluralize(period.years, "year")

        period.months > 0 ->
            pluralize(period.months, "month")

        period.days >= 7 ->
            pluralize(period.days / 7, "week")

        period.days > 0 ->
            pluralize(period.days, "day")

        period.hours > 0 ->
            pluralize(period.hours, "hour")

        period.minutes > 0 ->
            pluralize(period.minutes, "minute")

        period.seconds > 0 ->
            pluralize(period.seconds, "second")

        else -> "Just now"
    }
}

private fun pluralize(value: Int, unit: String): String {
    return if (value == 1) {
        "1 $unit ago"
    } else {
        "$value ${unit}s ago"
    }
}