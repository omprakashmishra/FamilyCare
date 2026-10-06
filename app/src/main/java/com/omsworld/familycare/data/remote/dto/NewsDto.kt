package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsItemDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "discription") val description: String? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "added_date") val addedDate: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "like_count") val likeCount: String? = null,
    @Json(name = "like") val like: String? = null,
    @Json(name = "image") val image: String? = null
)

@JsonClass(generateAdapter = true)
data class NewsListResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "news_list") val newsList: List<NewsItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class NewsLikeResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "message") val message: String? = null
)