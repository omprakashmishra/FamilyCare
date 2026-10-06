package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.ApiService
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getDiaryList(userId: String): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.show_diary,
            mapOf("user_id" to userId, "UserID" to userId)
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun addDiary(
        userId: String,
        subject: String,
        note: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.add_diary,
            mapOf(
                "user_id" to userId,
                "subject" to subject,
                "note" to note
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun editDiary(
        userId: String,
        noteId: String,
        subject: String,
        note: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.edit_diary,
            mapOf(
                "user_id" to userId,
                "note_id" to noteId,
                "subject" to subject,
                "note" to note
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun deleteDiary(userId: String, noteId: String): ApiResult<JSONObject> =
        safeApiCall {
            val raw = api.post(
                UrlList.delete_diary,
                mapOf("user_id" to userId, "note_id" to noteId)
            ).body() ?: ""
            JSONObject(raw)
        }
}