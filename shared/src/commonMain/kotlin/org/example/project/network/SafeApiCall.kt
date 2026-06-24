package org.example.project.network

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
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
                val body = response.body<CommonResponse<T>>()
                if (body.hasError) {
                    NetworkResult.Error(
                        message = body.message ?: "Something went wrong",
                        type = mapErrorCodeToType(body.errorCode),
                        errorCode = body.errorCode
                    )
                } else {
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
