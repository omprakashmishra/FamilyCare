package com.omsworld.familycare.data.repository

import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.remote.ApiService
import com.omsworld.familycare.data.remote.dto.LoginResponse
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val prefs: MySharedPreference
) {

    // ==================== Login ====================
    suspend fun login(email: String, password: String): ApiResult<LoginResponse> =
        safeApiCall {
            api.login(mapOf("email" to email, "password" to password))
                .body() ?: throw IllegalStateException("Empty response")
        }

    // ==================== OTP Verify ====================
    suspend fun verifyOtp(mobile: String, otp: String): ApiResult<LoginResponse> =
        safeApiCall {
            api.verifyOtp(mapOf("user_mob" to mobile, "otp" to otp))
                .body() ?: throw IllegalStateException("Empty response")
        }

    // ==================== Register ====================
    suspend fun register(
        userName: String,
        email: String,
        mobileNo: String,
        password: String
    ): ApiResult<LoginResponse> = safeApiCall {
        api.register(
            mapOf(
                "UserName" to userName,
                "Email" to email,
                "MobileNo" to mobileNo,
                "Password" to password
            )
        ).body() ?: throw IllegalStateException("Empty response")
    }

    // ==================== Generic POST ====================
    suspend fun postRaw(url: String, params: Map<String, String>): ApiResult<String> =
        safeApiCall { api.post(url, params).body() ?: "" }

    // ==================== Forgot Password ====================
    suspend fun sendForgotPassword(mobile: String): ApiResult<String> =
        safeApiCall {
            api.post(UrlList.forgot_pass, mapOf("mobile" to mobile)).body() ?: ""
        }

    // ==================== Resend OTP ====================
    suspend fun resendOtp(mobile: String): ApiResult<String> =
        safeApiCall {
            api.post(UrlList.resend_otp, mapOf("user_mob" to mobile)).body() ?: ""
        }

    // ==================== Save Session (typed DTO) ====================
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

    // ==================== ⬇️ THIS IS THE NEW METHOD ⬇️ ====================
    /**
     * Save user info from a raw JSON response.
     * Used by SignInUpViewModel when the API returns `user_info` as JSONObject.
     */
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
    // ==================== ⬆️ END NEW METHOD ⬆️ ====================

    // ==================== Session Status ====================
    fun isLoggedIn(): Boolean =
        prefs.getString(FamilyCareApp.appContext, Constants.LOGIN_STATUS, "0") == "1"

    // ==================== Logout ====================
    fun logout() {
        val ctx = FamilyCareApp.appContext
        val firebaseToken = prefs.getString(ctx, Constants.Firebasetoken, "0")
        prefs.clearSharedPreference(ctx)
        prefs.setString(ctx, Constants.Firebasetoken, firebaseToken)
        prefs.setString(ctx, Constants.LOGIN_STATUS, "0")
        prefs.setString(ctx, Constants.NOTIFICATION, "0")
        prefs.setString(ctx, Constants.LEGALAGREEMENTCHECK, "1")
    }
}