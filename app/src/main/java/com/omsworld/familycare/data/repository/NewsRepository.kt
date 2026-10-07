package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabaseNewsDto
import com.omsworld.familycare.data.remote.dto.SupabaseNewsLikeDto
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService
) {

    // ============================================================
    // GET NEWS LIST (news_list.php)
    // Returns news with an extra "like" flag showing if the current
    // user liked each item.
    // ============================================================
    suspend fun getNewsList(userId: String): ApiResult<List<NewsWithLike>> = safeApiCall {
        val news = supabase.getNews()
        val myLikes = supabase.getMyLikes(userEq = "eq.$userId")
        val likedIds = myLikes.map { it.newsId }.toSet()

        news.map {
            NewsWithLike(
                id = it.id?.toString() ?: "",
                title = it.title ?: "",
                discription = it.discription ?: "",
                category = it.category ?: "",
                addedDate = it.addedDate ?: "",
                newsType = it.type ?: "text",
                likeCount = it.likeCount?.toString() ?: "0",
                like = if (likedIds.contains(it.id?.toString())) "1" else "0",
                image = it.image ?: ""
            )
        }
    }

    // ============================================================
    // LIKE NEWS (like_news.php)
    // ============================================================
    suspend fun likeNews(userId: String, newsId: String): ApiResult<Unit> = safeApiCall {
        val response = supabase.likeNews(
            SupabaseNewsLikeDto(newsId = newsId, userId = userId)
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Like failed: ${response.code()}")
        }
    }

    // ============================================================
    // UNLIKE NEWS (unlike_news.php)
    // ============================================================
    suspend fun unlikeNews(userId: String, newsId: String): ApiResult<Unit> = safeApiCall {
        val response = supabase.unlikeNews(
            newsEq = "eq.$newsId",
            userEq = "eq.$userId"
        )
        if (!response.isSuccessful) {
            throw IllegalStateException("Unlike failed: ${response.code()}")
        }
    }
}

data class NewsWithLike(
    val id: String,
    val title: String,
    val discription: String,
    val category: String,
    val addedDate: String,
    val newsType: String,
    val likeCount: String,
    val like: String,
    val image: String
)