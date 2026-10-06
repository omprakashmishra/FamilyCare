package com.omsworld.familycare.core.result

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Wraps a suspend API call in try/catch → ApiResult.
 * Handles HTTP errors, timeouts, network failures gracefully.
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend () -> T
): ApiResult<T> = withContext(dispatcher) {
    try {
        ApiResult.Success(block())
    } catch (e: HttpException) {
        Timber.e(e, "HTTP error ${e.code()}")
        ApiResult.Error("Server error (${e.code()})", e.code(), e)
    } catch (e: UnknownHostException) {
        Timber.e(e, "Unknown host")
        ApiResult.Error("Server not reachable. Please try again.", null, e)
    } catch (e: SocketTimeoutException) {
        Timber.e(e, "Timeout")
        ApiResult.Error("Connection timed out. Please try again.", null, e)
    } catch (e: IOException) {
        Timber.e(e, "Network error")
        ApiResult.Error("Check your internet connection and try again.", null, e)
    } catch (e: Exception) {
        Timber.e(e, "Unexpected error")
        ApiResult.Error(e.message ?: "Something went wrong", null, e)
    }
}