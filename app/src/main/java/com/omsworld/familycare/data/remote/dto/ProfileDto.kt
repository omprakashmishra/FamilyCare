package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileDto(
    @Json(name = "UserName") val userName: String? = null,
    @Json(name = "MobileNumber") val mobileNumber: String? = null,
    @Json(name = "EmailID") val emailId: String? = null,
    @Json(name = "InvitationCode") val invitationCode: String? = null,
    @Json(name = "PaymentStatusCode") val paymentStatusCode: String? = null,
    @Json(name = "InviteeStatusID") val inviteeStatusId: String? = null
)

@JsonClass(generateAdapter = true)
data class ProfileResponse(
    @Json(name = "Status") val status: String? = null,
    @Json(name = "Message") val message: String? = null,
    @Json(name = "beforeDeleteMsg") val beforeDeleteMsg: String? = null,
    @Json(name = "beforeDeactivateMsg") val beforeDeactivateMsg: String? = null,
    @Json(name = "referralHelpMsg") val referralHelpMsg: String? = null,
    @Json(name = "Data") val data: List<ProfileDto>? = null
)

@JsonClass(generateAdapter = true)
data class ProfileUpdateResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "message") val message: String? = null
)