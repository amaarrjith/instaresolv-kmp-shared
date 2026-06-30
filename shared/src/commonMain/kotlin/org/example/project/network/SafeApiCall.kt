package org.example.project.network

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.client.statement.bodyAsText
import org.example.project.data.model.CommonResponse

fun HttpRequestBuilder.jsonBody(body: Any) {
    contentType(ContentType.Application.Json)
    setBody(body)
}

suspend inline fun <reified T> safeApiCall(
    crossinline call: suspend () -> HttpResponse
): NetworkResult<T> {
    return try {
        val response = call()
        val status = response.status
        when {
            status.isSuccess() -> {
                val bodyText = response.bodyAsText()
                val json = Json { ignoreUnknownKeys = true; isLenient = true }
                val jsonElement = try {
                    json.parseToJsonElement(bodyText)
                } catch (e: Exception) {
                    null
                }
                
                val hasError = jsonElement?.jsonObject?.get("hasError")?.jsonPrimitive?.booleanOrNull == true
                if (hasError) {
                    val errorCode = jsonElement.jsonObject["errorCode"]?.jsonPrimitive?.intOrNull
                    val message = jsonElement.jsonObject["message"]?.jsonPrimitive?.contentOrNull ?: "Something went wrong"
                    NetworkResult.Error(
                        message = message,
                        type = mapErrorCodeToType(errorCode),
                        errorCode = errorCode
                    )
                } else {
                    val body = json.decodeFromString<CommonResponse<T>>(bodyText)
                    val data = body.response
                    if (data != null) {
                        NetworkResult.Success(data)
                    } else {
                        NetworkResult.Error(
                            message = "Empty response body",
                            type = ErrorType.UNKNOWN
                        )
                    }
                }
            }
            status.value == 401 -> {
                NetworkResult.Error("Unauthorized", ErrorType.UNAUTHORIZED)
            }
            status.value == 422 -> {
                val errorMsg = try {
                    response.body<CommonResponse<T>>().message ?: "Validation failed"
                } catch (e: Exception) {
                    "Validation failed"
                }
                NetworkResult.Error(errorMsg, ErrorType.VALIDATION)
            }
            status.value >= 500 -> {
                NetworkResult.Error("Server error", ErrorType.SERVER)
            }
            else -> {
                NetworkResult.Error("HTTP Error: ${status.value}", ErrorType.UNKNOWN)
            }
        }
    } catch (e: io.ktor.client.plugins.HttpRequestTimeoutException) {
        NetworkResult.Error("Request timed out", ErrorType.TIMEOUT)
    } catch (e: io.ktor.client.network.sockets.ConnectTimeoutException) {
        NetworkResult.Error("Connection timed out", ErrorType.TIMEOUT)
    } catch (e: kotlinx.io.IOException) {
        NetworkResult.Error("Network error: check your internet connection", ErrorType.NETWORK)
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "An unexpected error occurred", ErrorType.UNKNOWN)
    }
}

fun mapErrorCodeToType(errorCode: Int?): ErrorType {
    return when (errorCode) {
        401 -> ErrorType.UNAUTHORIZED
        422 -> ErrorType.VALIDATION
        in 500..599 -> ErrorType.SERVER
        else -> ErrorType.UNKNOWN
    }
}
