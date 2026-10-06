package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.ApiService
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun pushLocation(
        userId: String,
        familyId: String,
        battery: String,
        lat: String,
        lng: String,
        address: String
    ): ApiResult<String> = safeApiCall {
        api.post(
            UrlList.insert_position,
            mapOf(
                "user_id" to userId,
                "family_id" to familyId,
                "Battery" to battery,
                "lat" to lat,
                "lng" to lng,
                "address" to address
            )
        ).body() ?: ""
    }

    suspend fun fetchFriends(userId: String): ApiResult<List<JSONObject>> = safeApiCall {
        val raw = api.post(
            UrlList.family_group_info,
            mapOf("user_id" to userId, "family_id" to "")
        ).body() ?: ""
        parseFriends(raw)
    }

    suspend fun whoIsTrackingMe(aspnetUserId: String): ApiResult<List<JSONObject>> =
        safeApiCall {
            val raw = api.post(
                UrlList.whoIsTrackingMe,
                mapOf("AspnetUserID" to aspnetUserId)
            ).body() ?: ""
            parseList(raw, "Data")
        }

    private fun parseFriends(raw: String): List<JSONObject> {
        val root = JSONObject(raw)
        if (root.optString("success") != "1") return emptyList()
        val arr = root.optJSONArray("freind_list") ?: return emptyList()
        val list = mutableListOf<JSONObject>()
        for (i in 0 until arr.length()) {
            arr.optJSONObject(i)?.let { list.add(it) }
        }
        return list
    }

    private fun parseList(raw: String, key: String): List<JSONObject> {
        val root = JSONObject(raw)
        val arr = root.optJSONArray(key) ?: return emptyList()
        val list = mutableListOf<JSONObject>()
        for (i in 0 until arr.length()) {
            arr.optJSONObject(i)?.let { list.add(it) }
        }
        return list
    }
}