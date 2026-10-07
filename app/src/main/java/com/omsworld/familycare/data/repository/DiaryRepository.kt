package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabaseDiaryDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class DiaryRepository @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService
) {

    suspend fun getDiaryList(userId: String): ApiResult<List<SupabaseDiaryDto>> =
        safeApiCall {
            supabase.getDiary(userEq = "eq.$userId")
        }

    suspend fun addDiary(
        userId: String,
        subject: String,
        note: String
    ): ApiResult<Unit> = safeApiCall {
        val entry = SupabaseDiaryDto(
            userId = userId,
            subject = subject,
            note = note,
            addedDate = nowIso()
        )
        val response = supabase.addDiary(entry)
        if (!response.isSuccessful) {
            throw IllegalStateException("Add diary failed: ${response.code()}")
        }
    }

    suspend fun editDiary(
        userId: String,
        noteId: String,
        subject: String,
        note: String
    ): ApiResult<Unit> = safeApiCall {
        val response = supabase.updateDiary(
            idEq = "eq.$noteId",
            updates = mapOf("subject" to subject, "note" to note)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Edit diary failed: ${response.code()}")
        }
    }

    suspend fun deleteDiary(userId: String, noteId: String): ApiResult<Unit> =
        safeApiCall {
            val response = supabase.deleteDiary(idEq = "eq.$noteId")
            if (!response.isSuccessful) {
                throw IllegalStateException("Delete diary failed: ${response.code()}")
            }
        }

    private fun nowIso(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date())
}