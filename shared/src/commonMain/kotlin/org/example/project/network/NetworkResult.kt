package org.example.project.network

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(
        val message: String,
        val type: ErrorType = ErrorType.UNKNOWN,
        val errorCode: Int? = null
    ) : NetworkResult<Nothing>()
}

enum class ErrorType {
    NETWORK,       // No internet, DNS failure
    TIMEOUT,       // Request timed out
    UNAUTHORIZED,  // 401 — token expired
    SERVER,        // 500+ server errors
    VALIDATION,    // 422 — API validation errors
    UNKNOWN        // Catch-all
}
