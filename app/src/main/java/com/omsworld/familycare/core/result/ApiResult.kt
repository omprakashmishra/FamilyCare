package com.omsworld.familycare.core.result

/**
 * Generic API result wrapper. Used by all repositories.
 */
sealed class ApiResult<out T> {

    data class Success<T>(val data: T) : ApiResult<T>()

    data class Error(
        val message: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : ApiResult<Nothing>()

    data object Loading : ApiResult<Nothing>()

    data object Empty : ApiResult<Nothing>()
}

inline fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) block(data)
    return this
}

inline fun <T> ApiResult<T>.onError(block: (String, Int?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) block(message, code)
    return this
}

inline fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is ApiResult.Success -> ApiResult.Success(transform(data))
    is ApiResult.Error   -> this
    ApiResult.Loading    -> ApiResult.Loading
    ApiResult.Empty      -> ApiResult.Empty
}

inline fun <T> ApiResult<T>.getOrNull(): T? =
    if (this is ApiResult.Success) data else null

inline fun <T> ApiResult<T>.getOrDefault(default: T): T =
    if (this is ApiResult.Success) data else default