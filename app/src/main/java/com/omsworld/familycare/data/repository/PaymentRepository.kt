package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.ApiService
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getPackages(countryCode: String, userName: String): ApiResult<JSONObject> =
        safeApiCall {
            val raw = api.post(
                UrlList.Package,
                mapOf("CountryCode" to countryCode, "UserName" to userName)
            ).body() ?: ""
            JSONObject(raw)
        }

    suspend fun applyPromoCode(
        packageId: String,
        promoCode: String,
        countryCodeId: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.promocode,
            mapOf(
                "PackageId" to packageId,
                "PromotionCode" to promoCode,
                "CountryCodeID" to countryCodeId
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun validateReferralCode(propertyValue: String): ApiResult<JSONObject> =
        safeApiCall {
            val raw = api.post(
                UrlList.ValidateReferralCode,
                mapOf("PropertyValue" to propertyValue)
            ).body() ?: ""
            JSONObject(raw)
        }

    suspend fun processPayment(
        userId: String,
        packageId: String,
        promoCode: String,
        deviceId: String,
        amount: String,
        referralCode: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.ProcessPayment,
            mapOf(
                "UserID" to userId,
                "PakageID" to packageId,
                "PromotionCode" to promoCode,
                "DeviceID" to deviceId,
                "Amount" to amount,
                "MessageType" to "Registration",
                "ReferralCode" to referralCode
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun updatePayment(
        subscriptionId: String,
        paymentStatus: String,
        paymentId: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.UPDATEPAYMENT,
            mapOf(
                "SubscriptionID" to subscriptionId,
                "PaymentStatus" to paymentStatus,
                "isAutoRenewal" to paymentId
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun resendMail(userId: String): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.ResendMail,
            mapOf("UserID" to userId, "user_id" to userId)
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun getUserInfo(userName: String): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.UserInfo,
            mapOf("UserName" to userName)
        ).body() ?: ""
        JSONObject(raw)
    }
}