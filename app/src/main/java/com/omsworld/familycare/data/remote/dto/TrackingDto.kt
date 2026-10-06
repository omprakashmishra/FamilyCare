package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrackingFriendDto(
    @Json(name = "user_id") val userId: String? = null,
    @Json(name = "user_name") val userName: String? = null,
    @Json(name = "user_mob") val userMob: String? = null,
    @Json(name = "user_img") val userImg: String? = null,
    @Json(name = "lat") val lat: String? = null,
    @Json(name = "lng") val lng: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "battery") val battery: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "time") val time: String? = null
)

@JsonClass(generateAdapter = true)
data class TrackingListResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "freind_list") val friendList: List<TrackingFriendDto>? = null
)

@JsonClass(generateAdapter = true)
data class WhoIsTrackingMeResponse(
    @Json(name = "Status") val status: Int? = null,
    @Json(name = "Data") val data: List<WhoTracksDto>? = null
)

@JsonClass(generateAdapter = true)
data class WhoTracksDto(
    @Json(name = "UserName") val userName: String? = null,
    @Json(name = "AspnetUserId") val aspnetUserId: String? = null,
    @Json(name = "istrackMe") val isTrackMe: String? = null
)