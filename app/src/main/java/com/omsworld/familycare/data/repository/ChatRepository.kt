package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.ApiService
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getChatList(userId: String): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.user_chat_list,
            mapOf("user_id" to userId, "UserID" to userId)
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun getChatDetails(
        userId: String,
        friendId: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.user_chat_details,
            mapOf("user_id" to userId, "freind_id" to friendId)
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun sendChat(
        userId: String,
        friendId: String,
        message: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.user_chat,
            mapOf(
                "user_id" to userId,
                "freind_id" to friendId,
                "message" to message,
                "type" to "text"
            )
        ).body() ?: ""
        JSONObject(raw)
    }
}