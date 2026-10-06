package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    @Json(name = "message_id") val messageId: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "sender_id") val senderId: String? = null,
    @Json(name = "time") val time: String? = null,
    @Json(name = "type") val type: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatUserDto(
    @Json(name = "freind_id") val friendId: String? = null,
    @Json(name = "freind_fullname") val friendFullName: String? = null,
    @Json(name = "freind_img") val friendImg: String? = null,
    @Json(name = "friend_phone") val friendPhone: String? = null,
    @Json(name = "message_id") val messageId: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "sender_id") val senderId: String? = null,
    @Json(name = "time") val time: String? = null,
    @Json(name = "type") val type: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatListResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "chat_user_info") val chatUserInfo: List<ChatUserDto>? = null
)

@JsonClass(generateAdapter = true)
data class ChatDetailsResponse(
    @Json(name = "status") val status: String? = null,
    @Json(name = "chat_info") val chatInfo: List<ChatMessageDto>? = null
)