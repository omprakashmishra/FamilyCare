package com.omsworld.familycare.data.repository

import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.LoginResponse
import com.omsworld.familycare.data.remote.dto.SupabaseUserDto
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService,
    private val prefs: MySharedPreference
) {

    suspend fun loginByMobile(mobile: String, password: String): ApiResult<SupabaseUserDto> =
        safeApiCall {
            val users = supabase.getUserByMobile(mobileEq = "eq.$mobile")
            val user = users.firstOrNull()
                ?: throw IllegalStateException("User not found")

            if (user.password != password) {
                throw IllegalStateException("Invalid credentials")
            }

            saveSession(user)
            user
        }

    suspend fun register(
        userId: String,
        userName: String,
        email: String,
        mobile: String,
        password: String
    ): ApiResult<Unit> = safeApiCall {
        val newUser = SupabaseUserDto(
            aspnetUserId = userId,
            email = email.ifBlank { null },
            mobileNo = mobile,
            userName = userName,
            password = password,
            inviteeStatusId = "1080"
        )
        val response = supabase.insertUser(newUser)
        if (!response.isSuccessful) {
            throw IllegalStateException("Registration failed: ${response.code()}")
        }
    }

    fun saveSession(user: SupabaseUserDto) {
        val ctx = FamilyCareApp.appContext
        prefs.setString(ctx, Constants.USER_ID, user.aspnetUserId ?: "")
        prefs.setString(ctx, Constants.USER_NAME, user.userName ?: "")
        prefs.setString(ctx, Constants.MOBILE_only, user.mobileNo ?: "")
        prefs.setString(ctx, Constants.EMAIL, user.email ?: "")
        prefs.setString(ctx, Constants.USER_IMAGE, user.userImg ?: "")
        prefs.setString(ctx, Constants.FAMILY_ID, user.familyId ?: "")
        prefs.setString(ctx, Constants.FAMILY_NAME, user.familyName ?: "")
        prefs.setString(
            ctx,
            Constants.IsFamilyAdmin,
            if (user.isFamilyAdmin == true) "1" else "0"
        )
        prefs.setString(ctx, Constants.LOGIN_STATUS, "1")
    }

    fun saveSession(res: LoginResponse) {
        val ctx = FamilyCareApp.appContext
        res.userInfo?.let { u ->
            prefs.setString(ctx, Constants.USER_ID, u.userId ?: "")
            prefs.setString(ctx, Constants.USER_NAME, u.userName ?: "")
            prefs.setString(ctx, Constants.MOBILE_only, u.userMob ?: "")
            prefs.setString(ctx, Constants.EMAIL, u.userEmail ?: "")
            prefs.setString(ctx, Constants.USER_IMAGE, u.userImg ?: "")
            prefs.setString(ctx, Constants.FAMILY_ID, u.familyId ?: "")
            prefs.setString(ctx, Constants.FAMILY_NAME, u.familyName ?: "")
            prefs.setString(ctx, Constants.IsFamilyAdmin, u.isFamilyAdmin ?: "0")
        }
        prefs.setString(ctx, Constants.LOGIN_STATUS, "1")
    }

    fun isLoggedIn(): Boolean =
        prefs.getString(FamilyCareApp.appContext, Constants.LOGIN_STATUS) == "1"

    fun logout() {
        val ctx = FamilyCareApp.appContext
        val firebaseToken = prefs.getString(ctx, Constants.Firebasetoken)
        prefs.clearSharedPreference(ctx)
        prefs.setString(ctx, Constants.Firebasetoken, firebaseToken)
        prefs.setString(ctx, Constants.LOGIN_STATUS, "0")
    }

    suspend fun verifyOtp(mobile: String, otp: String): ApiResult<LoginResponse> =
        ApiResult.Error("OTP verification is being migrated. Please try again later.")

    suspend fun postRaw(url: String, params: Map<String, String>): ApiResult<String> =
        ApiResult.Error("This feature is being migrated. Please try again later.")

    suspend fun sendForgotPassword(mobile: String): ApiResult<String> =
        ApiResult.Error("Forgot password is being migrated. Please try again later.")

    suspend fun resendOtp(mobile: String): ApiResult<String> =
        ApiResult.Error("Resend OTP is being migrated. Please try again later.")

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> =
        ApiResult.Error("Legacy login is disabled. Use Supabase login.")

    fun persistUserInfo(userInfo: JSONObject) {
        val ctx = FamilyCareApp.appContext
        prefs.setString(ctx, Constants.USER_ID, userInfo.optString("user_id"))
        prefs.setString(ctx, Constants.USER_NAME, userInfo.optString("user_name"))
        prefs.setString(ctx, Constants.MOBILE_only, userInfo.optString("user_mob"))
        prefs.setString(ctx, Constants.EMAIL, userInfo.optString("user_email"))
        prefs.setString(ctx, Constants.USER_IMAGE, userInfo.optString("user_img"))
        prefs.setString(ctx, Constants.FAMILY_ID, userInfo.optString("family_id"))
        prefs.setString(ctx, Constants.FAMILY_NAME, userInfo.optString("family_name"))
        prefs.setString(ctx, Constants.IsFamilyAdmin, userInfo.optString("isFamilyAdmin", "0"))
        prefs.setString(ctx, Constants.LOGIN_STATUS, "1")
    }
}
