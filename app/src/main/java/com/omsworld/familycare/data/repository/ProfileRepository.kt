package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabaseSuggestionDto
import com.omsworld.familycare.data.remote.dto.SupabaseUserDto
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService
) {

    suspend fun getProfile(userId: String): ApiResult<SupabaseUserDto> = safeApiCall {
        supabase.getUserById(idEq = "eq.$userId").firstOrNull()
            ?: throw IllegalStateException("Profile not found")
    }

    suspend fun updateProfile(
        userId: String,
        userName: String,
        email: String,
        dob: String,
        about: String
    ): ApiResult<Unit> = safeApiCall {
        val updates = mutableMapOf<String, Any>(
            "user_name" to userName,
            "user_email" to email
        )
        if (dob.isNotBlank()) updates["dob"] = dob
        if (about.isNotBlank()) updates["about"] = about

        val response = supabase.updateUser("eq.$userId", updates)
        if (!response.isSuccessful) {
            throw IllegalStateException("Update failed: ${response.code()}")
        }
    }

    suspend fun uploadImage(userId: String, base64Image: String): ApiResult<String> =
        safeApiCall {
            val response = supabase.updateUser(
                "eq.$userId",
                mapOf("user_img" to base64Image)
            )
            if (!response.isSuccessful) {
                throw IllegalStateException("Image upload failed: ${response.code()}")
            }
            base64Image
        }

    suspend fun changePassword(
        userId: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): ApiResult<Unit> = safeApiCall {
        if (newPassword != confirmPassword) {
            throw IllegalStateException("Passwords do not match")
        }
        val user = supabase.getUserById(idEq = "eq.$userId").firstOrNull()
            ?: throw IllegalStateException("User not found")

        if (user.password != oldPassword) {
            throw IllegalStateException("Old password is incorrect")
        }

        val response = supabase.updateUser("eq.$userId", mapOf("password" to newPassword))
        if (!response.isSuccessful) {
            throw IllegalStateException("Password change failed: ${response.code()}")
        }
    }

    suspend fun deleteAccount(
        aspnetUserId: String,
        password: String,
        type: String
    ): ApiResult<Unit> = safeApiCall {
        val user = supabase.getUserById(idEq = "eq.$aspnetUserId").firstOrNull()
            ?: throw IllegalStateException("User not found")

        if (user.password != password) {
            throw IllegalStateException("Incorrect password")
        }

        if (type.equals("DELETE", ignoreCase = true)) {
            supabase.deleteUser("eq.$aspnetUserId")
        } else {
            supabase.updateUser(
                "eq.$aspnetUserId",
                mapOf("invitee_status_id" to "deactivated")
            )
        }
        Unit
    }

    suspend fun registerDevice(
        userId: String,
        deviceId: String,
        tokenId: String
    ): ApiResult<Unit> = safeApiCall {
        val response = supabase.updateUser(
            "eq.$userId",
            mapOf("device_id" to deviceId, "firebase_token" to tokenId)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Device registration failed: ${response.code()}")
        }
    }

    suspend fun alarmOnOff(
        aspnetUserId: String,
        isAlarmMute: String,
        isFriendsTracking: String
    ): ApiResult<Unit> = safeApiCall {
        val response = supabase.updateUser(
            "eq.$aspnetUserId",
            mapOf(
                "is_alarm_mute" to isAlarmMute,
                "is_friends_tracking" to isFriendsTracking
            )
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Alarm update failed: ${response.code()}")
        }
    }

    suspend fun submitSuggestion(userId: String, message: String): ApiResult<Unit> =
        safeApiCall {
            val response = supabase.submitSuggestion(
                SupabaseSuggestionDto(userId = userId, message = message)
            )
            if (!response.isSuccessful) {
                throw IllegalStateException("Suggestion failed: ${response.code()}")
            }
        }
}