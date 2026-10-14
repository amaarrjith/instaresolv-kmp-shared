package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommonModelResponse(
    val isSuccess: Boolean? = false,
    val statusMessage: String? = "",
    val excelUrl: String? = null,
    val pdfUrl: String? = null,
    @SerialName("pdf_url")
    val pdf_url: String? = null,
    @SerialName("excel_url")
    val excel_url: String? = null,
    val url: String? = null,
    @SerialName("file_url")
    val file_url: String? = null,
    val fileUrl: String? = null,
    val pdf: String? = null
) {
    val resolvedPdfUrl: String?
        get() = pdfUrl ?: pdf_url ?: pdf ?: excelUrl ?: excel_url ?: url ?: file_url ?: fileUrl

    val resolvedExcelUrl: String?
        get() = excelUrl ?: excel_url ?: url ?: file_url ?: fileUrl ?: pdfUrl ?: pdf_url
}