package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "status") val status: String? = null,
    @Json(name = "success") val success: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: T? = null
) {
    val isSuccess: Boolean
        get() = status == "1" || success == "1"
}