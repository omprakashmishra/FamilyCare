package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FamilyMemberDto(
    @Json(name = "user_id") val userId: String? = null,
    @Json(name = "user_name") val userName: String? = null,
    @Json(name = "user_mob") val userMob: String? = null,
    @Json(name = "user_img") val userImg: String? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "time") val time: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "member_status") val memberStatus: String? = null,
    @Json(name = "family_id") val familyId: String? = null,
    @Json(name = "family_name") val familyName: String? = null,
    @Json(name = "lat") val lat: String? = null,
    @Json(name = "lng") val lng: String? = null
)

@JsonClass(generateAdapter = true)
data class FamilyGroupResponse(
    @Json(name = "success") val success: String? = null,
    @Json(name = "family_id") val familyId: String? = null,
    @Json(name = "family_name") val familyName: String? = null,
    @Json(name = "isFamilyAdmin") val isFamilyAdmin: String? = null,
    @Json(name = "freind_list") val friendList: List<FamilyMemberDto>? = null,
    @Json(name = "request_list") val requestList: List<FamilyMemberDto>? = null
)

@JsonClass(generateAdapter = true)
data class FamilyRequestActionResponse(
    @Json(name = "success") val success: String? = null,
    @Json(name = "message") val message: String? = null
)