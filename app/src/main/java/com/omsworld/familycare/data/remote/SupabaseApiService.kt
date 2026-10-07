package com.omsworld.familycare.data.remote

import com.omsworld.familycare.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface SupabaseApiService {

    // ==================== USERS ====================
    @GET("rest/v1/users")
    suspend fun getUserByMobile(
        @Query("mobile_no") mobileEq: String,
        @Query("select") select: String = "*",
        @Query("limit") limit: Int = 1
    ): List<SupabaseUserDto>

    @GET("rest/v1/users")
    suspend fun getUserById(
        @Query("aspnet_user_id") idEq: String,
        @Query("select") select: String = "*",
        @Query("limit") limit: Int = 1
    ): List<SupabaseUserDto>

    @GET("rest/v1/users")
    suspend fun getFamilyMembers(
        @Query("family_id") familyEq: String,
        @Query("select") select: String = "*"
    ): List<SupabaseUserDto>

    @POST("rest/v1/users")
    suspend fun insertUser(
        @Body user: SupabaseUserDto
    ): Response<Unit>

    @PATCH("rest/v1/users")
    suspend fun updateUser(
        @Query("aspnet_user_id") idEq: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    // ⬇️ ADDED: deleteUser
    @DELETE("rest/v1/users")
    suspend fun deleteUser(
        @Query("aspnet_user_id") idEq: String
    ): Response<Unit>

    // ==================== DEVICES ====================
    @POST("rest/v1/devices")
    suspend fun insertDevice(@Body device: SupabaseDeviceDto): Response<Unit>

    @GET("rest/v1/devices")
    suspend fun getDeviceForUser(
        @Query("user_id") userEq: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "last_seen.desc",
        @Query("limit") limit: Int = 1
    ): List<SupabaseDeviceDto>

    @GET("rest/v1/devices")
    suspend fun getFamilyDevices(
        @Query("family_id") familyEq: String,
        @Query("select") select: String = "*"
    ): List<SupabaseDeviceDto>

    @PATCH("rest/v1/devices")
    suspend fun updateDevice(
        @Query("user_id") userEq: String,
        @Body updates: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>

    // ==================== FAMILY REQUESTS ====================
    @POST("rest/v1/family_requests")
    suspend fun insertFamilyRequest(@Body request: SupabaseFamilyRequestDto): Response<Unit>

    @GET("rest/v1/family_requests")
    suspend fun getPendingRequests(
        @Query("family_id") familyEq: String,
        @Query("is_accepted") acceptedEq: String = "eq.0",
        @Query("is_declined") declinedEq: String = "eq.0",
        @Query("select") select: String = "*"
    ): List<SupabaseFamilyRequestDto>

    @PATCH("rest/v1/family_requests")
    suspend fun updateFamilyRequest(
        @Query("id") idEq: String,
        @Body updates: Map<String, String>
    ): Response<Unit>

    // ==================== PHONE BOOK ====================
    @GET("rest/v1/phone_book")
    suspend fun getPhoneBook(
        @Query("user_id") userEq: String,
        @Query("select") select: String = "*"
    ): List<SupabasePhoneBookDto>

    @GET("rest/v1/phone_book")
    suspend fun getFamilyPhoneBook(
        @Query("family_id") familyEq: String,
        @Query("select") select: String = "*"
    ): List<SupabasePhoneBookDto>

    @POST("rest/v1/phone_book")
    suspend fun addContact(@Body contact: SupabasePhoneBookDto): Response<Unit>

    @PATCH("rest/v1/phone_book")
    suspend fun editContact(
        @Query("id") idEq: String,
        @Body updates: Map<String, String>
    ): Response<Unit>

    @DELETE("rest/v1/phone_book")
    suspend fun deleteContact(@Query("id") idEq: String): Response<Unit>

    // ==================== CHAT ====================
    @GET("rest/v1/chat_messages")
    suspend fun getChatHistory(
        @Query("or") or: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "sent_at.asc"
    ): List<SupabaseChatDto>

    @POST("rest/v1/chat_messages")
    suspend fun sendChat(@Body message: SupabaseChatDto): Response<Unit>

    // ==================== DIARY ====================
    @GET("rest/v1/diary")
    suspend fun getDiary(
        @Query("user_id") userEq: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "added_date.desc"
    ): List<SupabaseDiaryDto>

    @POST("rest/v1/diary")
    suspend fun addDiary(@Body entry: SupabaseDiaryDto): Response<Unit>

    @PATCH("rest/v1/diary")
    suspend fun updateDiary(
        @Query("id") idEq: String,
        @Body updates: Map<String, String>
    ): Response<Unit>

    @DELETE("rest/v1/diary")
    suspend fun deleteDiary(@Query("id") idEq: String): Response<Unit>

    // ==================== GROCERY ====================
    // ⬇️ UPDATED: added optional is_shopped filter
    @GET("rest/v1/grocery_items")
    suspend fun getGroceries(
        @Query("user_id") userEq: String,
        @Query("is_shopped") isShoppedEq: String? = null,
        @Query("select") select: String = "*",
        @Query("order") order: String = "added_date.desc"
    ): List<SupabaseGroceryDto>

    @GET("rest/v1/grocery_items")
    suspend fun getFamilyGroceries(
        @Query("family_id") familyEq: String,
        @Query("select") select: String = "*"
    ): List<SupabaseGroceryDto>

    @POST("rest/v1/grocery_items")
    suspend fun addGrocery(@Body item: SupabaseGroceryDto): Response<Unit>

    @PATCH("rest/v1/grocery_items")
    suspend fun updateGrocery(
        @Query("id") idEq: String,
        @Body updates: Map<String, String>
    ): Response<Unit>

    @DELETE("rest/v1/grocery_items")
    suspend fun deleteGrocery(@Query("id") idEq: String): Response<Unit>

    // ==================== NEWS ====================
    @GET("rest/v1/news")
    suspend fun getNews(
        @Query("select") select: String = "*",
        @Query("order") order: String = "added_date.desc"
    ): List<SupabaseNewsDto>

    @POST("rest/v1/news_likes")
    suspend fun likeNews(@Body like: SupabaseNewsLikeDto): Response<Unit>

    @DELETE("rest/v1/news_likes")
    suspend fun unlikeNews(
        @Query("news_id") newsEq: String,
        @Query("user_id") userEq: String
    ): Response<Unit>

    @GET("rest/v1/news_likes")
    suspend fun getMyLikes(
        @Query("user_id") userEq: String,
        @Query("select") select: String = "*"
    ): List<SupabaseNewsLikeDto>

    // ==================== SHOPPING ====================
    @GET("rest/v1/shopping_sites")
    suspend fun getShoppingSites(
        @Query("category_id") catEq: String,
        @Query("select") select: String = "*"
    ): List<SupabaseShoppingSiteDto>

    @GET("rest/v1/shopping_categories")
    suspend fun getShoppingCategories(
        @Query("select") select: String = "*"
    ): List<SupabaseShoppingCategoryDto>

    // ==================== PAYMENT ====================
    @POST("rest/v1/payment_history")
    suspend fun recordPayment(@Body payment: SupabasePaymentDto): Response<Unit>

    @PATCH("rest/v1/payment_history")
    suspend fun updatePaymentHistory(
        @Query("subscription_id") subEq: String,
        @Body updates: Map<String, String>
    ): Response<Unit>

    // ==================== SUGGESTIONS ====================
    @POST("rest/v1/user_suggestions")
    suspend fun submitSuggestion(@Body suggestion: SupabaseSuggestionDto): Response<Unit>

    @DELETE("rest/v1/family_requests")
    suspend fun deleteFamilyRequest(
        @Query("id") idEq: String
    ): Response<Unit>

}