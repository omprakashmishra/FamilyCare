package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// ==================== USERS ====================
@JsonClass(generateAdapter = true)
data class SupabaseUserDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "aspnet_user_id") val aspnetUserId: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "mobile_no") val mobileNo: String? = null,
    @Json(name = "user_name") val userName: String? = null,
    @Json(name = "password") val password: String? = null,
    @Json(name = "family_id") val familyId: String? = null,
    @Json(name = "family_name") val familyName: String? = null,
    @Json(name = "is_family_admin") val isFamilyAdmin: Boolean? = null,
    @Json(name = "user_img") val userImg: String? = null,
    @Json(name = "dob") val dob: String? = null,
    @Json(name = "about") val about: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "activation_code") val activationCode: String? = null,
    @Json(name = "invitee_status_id") val inviteeStatusId: String? = null,
    @Json(name = "payment_status_code") val paymentStatusCode: String? = null,
    @Json(name = "device_id") val deviceId: String? = null,
    @Json(name = "firebase_token") val firebaseToken: String? = null,
    @Json(name = "is_alarm_mute") val isAlarmMute: String? = null,
    @Json(name = "is_friends_tracking") val isFriendsTracking: String? = null
)

// ==================== DEVICES ====================
@JsonClass(generateAdapter = true)
data class SupabaseDeviceDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "user_id") val userId: String,
    @Json(name = "family_id") val familyId: String? = null,
    @Json(name = "battery") val battery: String? = null,
    @Json(name = "lat") val lat: String? = null,
    @Json(name = "lng") val lng: String? = null,
    @Json(name = "address") val address: String? = null,
    @Json(name = "online_status") val onlineStatus: String? = "Offline",
    @Json(name = "last_seen") val lastSeen: String? = null
)

// ==================== FAMILY REQUESTS ====================
@JsonClass(generateAdapter = true)
data class SupabaseFamilyRequestDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "from_phone") val fromPhone: String,
    @Json(name = "to_phone") val toPhone: String,
    @Json(name = "user_id") val userId: String? = null,
    @Json(name = "family_id") val familyId: String? = null,
    @Json(name = "member_mob") val memberMob: String? = null,
    @Json(name = "action") val action: String? = null,
    @Json(name = "is_accepted") val isAccepted: String? = "0",
    @Json(name = "is_declined") val isDeclined: String? = "0",
    @Json(name = "created_at") val createdAt: String? = null
)

// ==================== PHONE BOOK ====================
@JsonClass(generateAdapter = true)
data class SupabasePhoneBookDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "user_id") val userId: String,
    @Json(name = "family_id") val familyId: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "comment") val comment: String? = null,
    @Json(name = "added_by") val addedBy: String? = null,
    @Json(name = "added_by_name") val addedByName: String? = null
)

// ==================== CHAT ====================
@JsonClass(generateAdapter = true)
data class SupabaseChatDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "message_id") val messageId: String? = null,
    @Json(name = "user_id") val userId: String,
    @Json(name = "friend_id") val friendId: String,
    @Json(name = "message") val message: String? = null,
    @Json(name = "type") val type: String? = "text",
    @Json(name = "sent_at") val sentAt: String? = null
)

// ==================== DIARY ====================
@JsonClass(generateAdapter = true)
data class SupabaseDiaryDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "user_id") val userId: String,
    @Json(name = "note_id") val noteId: String? = null,
    @Json(name = "subject") val subject: String? = null,
    @Json(name = "note") val note: String? = null,
    @Json(name = "added_date") val addedDate: String? = null
)

// ==================== GROCERY ====================
@JsonClass(generateAdapter = true)
data class SupabaseGroceryDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "user_id") val userId: String,
    @Json(name = "family_id") val familyId: String? = null,
    @Json(name = "note") val note: String? = null,
    @Json(name = "is_shopped") val isShopped: String? = "0",
    @Json(name = "price") val price: String? = null,
    @Json(name = "added_by") val addedBy: String? = null,
    @Json(name = "added_by_name") val addedByName: String? = null,
    @Json(name = "added_date") val addedDate: String? = null
)

// ==================== NEWS ====================
@JsonClass(generateAdapter = true)
data class SupabaseNewsDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "discription") val discription: String? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "image") val image: String? = null,
    @Json(name = "like_count") val likeCount: Int? = 0,
    @Json(name = "added_date") val addedDate: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseNewsLikeDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "news_id") val newsId: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "liked_at") val likedAt: String? = null
)

// ==================== SHOPPING ====================
@JsonClass(generateAdapter = true)
data class SupabaseShoppingSiteDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "image") val image: String? = null,
    @Json(name = "category_id") val categoryId: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseShoppingCategoryDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "image") val image: String? = null
)

// ==================== PAYMENT ====================
@JsonClass(generateAdapter = true)
data class SupabasePaymentDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "user_id") val userId: String,
    @Json(name = "package_id") val packageId: String? = null,
    @Json(name = "promotion_code") val promotionCode: String? = null,
    @Json(name = "device_id") val deviceId: String? = null,
    @Json(name = "amount") val amount: String? = null,
    @Json(name = "subscription_id") val subscriptionId: String? = null,
    @Json(name = "payment_status") val paymentStatus: String? = null,
    @Json(name = "referral_code") val referralCode: String? = null
)

// ==================== SUGGESTIONS ====================
@JsonClass(generateAdapter = true)
data class SupabaseSuggestionDto(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "user_id") val userId: String,
    @Json(name = "message") val message: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)