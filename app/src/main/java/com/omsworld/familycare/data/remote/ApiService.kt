package com.omsworld.familycare.data.remote

import com.omsworld.familycare.data.remote.dto.LoginResponse
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Retrofit service. Uses @Url so we can hit any endpoint in UrlList.
 */
interface ApiService {

    /** Generic POST — works with all legacy PHP endpoints. */
    @FormUrlEncoded
    @POST
    suspend fun post(
        @Url url: String,
        @FieldMap params: Map<String, String>
    ): Response<String>

    // ========== Typed endpoints (recommended) ==========

    @FormUrlEncoded
    @POST("log.php")
    suspend fun login(
        @FieldMap params: Map<String, String>
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("verify_otp.php")
    suspend fun verifyOtp(
        @FieldMap params: Map<String, String>
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST("reg_with_mob.php")
    suspend fun register(
        @FieldMap params: Map<String, String>
    ): Response<LoginResponse>
}