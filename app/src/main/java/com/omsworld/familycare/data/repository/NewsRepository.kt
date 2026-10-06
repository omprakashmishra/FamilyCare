package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.ApiService
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getNewsList(userId: String): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.news_list,
            mapOf("user_id" to userId)
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun likeNews(userId: String, newsId: String): ApiResult<JSONObject> =
        safeApiCall {
            val raw = api.post(
                UrlList.like_news,
                mapOf("user_id" to userId, "news_id" to newsId)
            ).body() ?: ""
            JSONObject(raw)
        }

    suspend fun unlikeNews(userId: String, newsId: String): ApiResult<JSONObject> =
        safeApiCall {
            val raw = api.post(
                UrlList.unlike_news,
                mapOf("user_id" to userId, "news_id" to newsId)
            ).body() ?: ""
            JSONObject(raw)
        }
}