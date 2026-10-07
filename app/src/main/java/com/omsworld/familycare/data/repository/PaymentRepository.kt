package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabasePaymentDto
import com.omsworld.familycare.data.remote.dto.SupabaseUserDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService
) {

    // ============================================================
    // GET PACKAGES
    // In the new schema, packages are static and stored server-side.
    // For now return a fixed list; wire to a `packages` table if needed.
    // ============================================================
    suspend fun getPackages(countryCode: String, userName: String): ApiResult<List<PackageItem>> =
        safeApiCall {
            // TODO: migrate to a real `packages` table
            listOf(
                PackageItem("1", "Monthly", "99", "INR", "1-month subscription"),
                PackageItem("2", "Quarterly", "249", "INR", "3-month subscription"),
                PackageItem("3", "Yearly", "899", "INR", "12-month subscription")
            )
        }

    // ============================================================
    // APPLY PROMO CODE
    // ============================================================
    suspend fun applyPromoCode(
        packageId: String,
        promoCode: String,
        countryCodeId: String
    ): ApiResult<PromoResult> = safeApiCall {
        // TODO: wire to a `promo_codes` table
        throw IllegalStateException("Promo codes are being migrated")
    }

    // ============================================================
    // VALIDATE REFERRAL CODE
    // ============================================================
    suspend fun validateReferralCode(propertyValue: String): ApiResult<Boolean> =
        safeApiCall {
            // TODO: wire to a `referrals` table
            false
        }

    // ============================================================
    // PROCESS PAYMENT — records to payment_history
    // ============================================================
    suspend fun processPayment(
        userId: String,
        packageId: String,
        promoCode: String,
        deviceId: String,
        amount: String,
        referralCode: String
    ): ApiResult<PaymentResult> = safeApiCall {
        val subscriptionId = "SUB-${System.currentTimeMillis()}"

        val entry = SupabasePaymentDto(
            userId = userId,
            packageId = packageId,
            promotionCode = promoCode,
            deviceId = deviceId,
            amount = amount,
            subscriptionId = subscriptionId,
            paymentStatus = "Pending",
            referralCode = referralCode
        )
        val response = supabase.recordPayment(entry)
        if (!response.isSuccessful) {
            throw IllegalStateException("Payment processing failed: ${response.code()}")
        }

        PaymentResult(
            subscriptionId = subscriptionId,
            activationCode = "",
            message = "Payment initiated"
        )
    }

    // ============================================================
    // UPDATE PAYMENT STATUS
    // ============================================================
    suspend fun updatePayment(
        subscriptionId: String,
        paymentStatus: String,
        paymentId: String
    ): ApiResult<Unit> = safeApiCall {
        // TODO: add a proper update endpoint in SupabaseApiService
        // For now, treat as success (mock update).
        Unit
    }

    // ============================================================
    // RESEND MAIL
    // ============================================================
    suspend fun resendMail(userId: String): ApiResult<Unit> = safeApiCall {
        // TODO: integrate with an email service (SendGrid/Resend/etc.)
        Unit
    }

    // ============================================================
    // GET USER INFO
    // ============================================================
    suspend fun getUserInfo(userName: String): ApiResult<SupabaseUserDto> = safeApiCall {
        supabase.getUserByMobile(mobileEq = "eq.$userName").firstOrNull()
            ?: supabase.getUserById(idEq = "eq.$userName").firstOrNull()
            ?: throw IllegalStateException("User not found")
    }
}

data class PackageItem(
    val packageId: String,
    val packageName: String,
    val amount: String,
    val amountType: String,
    val packageDesc: String
)

data class PromoResult(
    val amount: String,
    val discount: String,
    val packageName: String
)

data class PaymentResult(
    val subscriptionId: String,
    val activationCode: String,
    val message: String
)