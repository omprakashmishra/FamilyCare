package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryNoteDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "note") val note: String? = null,
    @Json(name = "subject") val subject: String? = null,
    @Json(name = "added_date") val addedDate: String? = null
)

@JsonClass(generateAdapter = true)
data class DiaryListResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "my_diary") val myDiary: List<DiaryNoteDto>? = null
)

@JsonClass(generateAdapter = true)
data class DiaryActionResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "message") val message: String? = null
)