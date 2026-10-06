package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserInfoDto(
    @Json(name = "user_id") val userId: String? = null,
    @Json(name = "user_email") val userEmail: String? = null,
    @Json(name = "user_name") val userName: String? = null,
    @Json(name = "user_mob") val userMob: String? = null,
    @Json(name = "user_img") val userImg: String? = null,
    @Json(name = "family_id") val familyId: String? = null,
    @Json(name = "family_name") val familyName: String? = null,
    @Json(name = "isFamilyAdmin") val isFamilyAdmin: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "user_info") val userInfo: UserInfoDto? = null,
    @Json(name = "mobile") val mobile: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "UserName") val userName: String,
    @Json(name = "Email") val email: String,
    @Json(name = "MobileNo") val mobileNo: String,
    @Json(name = "Password") val password: String
)

@JsonClass(generateAdapter = true)
data class VerifyOtpRequest(
    @Json(name = "user_mob") val userMob: String,
    @Json(name = "otp") val otp: String
)