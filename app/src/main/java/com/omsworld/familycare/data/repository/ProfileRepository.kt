package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.ApiService
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getProfile(userId: String): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.GetProfile,
            mapOf("UserID" to userId, "user_id" to userId)
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun updateProfile(
        userId: String,
        userName: String,
        email: String,
        dob: String,
        about: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.profile_edit,
            mapOf(
                "user_id" to userId,
                "user_name" to userName,
                "user_email" to email,
                "dob" to dob,
                "about" to about,
                "gender" to "."
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun uploadImage(userId: String, base64Image: String): ApiResult<JSONObject> =
        safeApiCall {
            val raw = api.post(
                UrlList.upload_image,
                mapOf("user_id" to userId, "user_img" to base64Image)
            ).body() ?: ""
            JSONObject(raw)
        }

    suspend fun changePassword(
        userId: String,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.change_pass,
            mapOf(
                "user_id" to userId,
                "old_pass" to oldPassword,
                "new_pass" to newPassword
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun deleteAccount(
        aspnetUserId: String,
        password: String,
        type: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.DeleteAccount,
            mapOf(
                "AspnetUserID" to aspnetUserId,
                "Password" to password,
                "Type" to type
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun registerDevice(
        userId: String,
        deviceId: String,
        tokenId: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.push_registration,
            mapOf(
                "user_id" to userId,
                "device_id" to deviceId,
                "token_id" to tokenId
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun alarmOnOff(
        aspnetUserId: String,
        isAlarmMute: String,
        isFriendsTracking: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.ALARMONOFFREQUEST,
            mapOf(
                "AspnetUserID" to aspnetUserId,
                "IsAlarmMute" to isAlarmMute,
                "IsFriendsTracking" to isFriendsTracking
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun submitSuggestion(
        userId: String,
        message: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.user_suggestion,
            mapOf("user_id" to userId, "message" to message)
        ).body() ?: ""
        JSONObject(raw)
    }
}