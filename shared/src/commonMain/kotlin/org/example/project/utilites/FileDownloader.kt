package org.example.project.utilites

import androidx.compose.runtime.Composable
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

expect class FileDownloader {
    fun downloadFile(url: String, fileName: String)
}

@Composable
expect fun rememberFileDownloader(): FileDownloader

fun getTodayFormattedDate(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val day = now.dayOfMonth.toString().padStart(2, '0')
    val month = now.monthNumber.toString().padStart(2, '0')
    val year = now.year
    return "$day-$month-$year"
}

fun getExcelReportFileName(moduleName: String, url: String = ""): String {
    val cleanUrl = url.substringBefore('?').substringBefore('#')
    val ext = when {
        cleanUrl.endsWith(".xlsx", ignoreCase = true) -> "xlsx"
        cleanUrl.endsWith(".xls", ignoreCase = true) -> "xls"
        cleanUrl.endsWith(".csv", ignoreCase = true) -> "csv"
        moduleName.equals("Permit", ignoreCase = true) -> "xlsx"
        else -> "csv"
    }
    return "$moduleName Report ${getTodayFormattedDate()}.$ext"
}

fun getPdfReportFileName(moduleName: String, itemId: Any?): String {
    val idStr = itemId?.toString()?.trim().orEmpty()
    return if (idStr.isNotBlank()) {
        "$moduleName Report ID - $idStr.pdf"
    } else {
        "$moduleName Report.pdf"
    }
}
