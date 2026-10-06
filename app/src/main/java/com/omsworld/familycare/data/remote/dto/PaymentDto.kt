package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PackageDto(
    @Json(name = "PackageID") val packageId: String? = null,
    @Json(name = "PackageName") val packageName: String? = null,
    @Json(name = "Amount") val amount: String? = null,
    @Json(name = "AmountType") val amountType: String? = null,
    @Json(name = "PackageDesc") val packageDesc: String? = null
)

@JsonClass(generateAdapter = true)
data class PackageResponse(
    @Json(name = "Status") val status: Int? = null,
    @Json(name = "Message") val message: String? = null,
    @Json(name = "Data") val data: List<PackageDto>? = null
)

@JsonClass(generateAdapter = true)
data class PromoCodeResponse(
    @Json(name = "Status") val status: String? = null,
    @Json(name = "Message") val message: String? = null,
    @Json(name = "Data") val data: PromoCodeData? = null
)

@JsonClass(generateAdapter = true)
data class PromoCodeData(
    @Json(name = "Amount") val amount: String? = null,
    @Json(name = "Discount") val discount: String? = null,
    @Json(name = "PackageName") val packageName: String? = null
)

@JsonClass(generateAdapter = true)
data class PaymentProcessResponse(
    @Json(name = "Status") val status: Int? = null,
    @Json(name = "Message") val message: String? = null,
    @Json(name = "Data") val data: List<PaymentDataDto>? = null
)

@JsonClass(generateAdapter = true)
data class PaymentDataDto(
    @Json(name = "ActivationCode") val activationCode: String? = null,
    @Json(name = "SubscriptionID") val subscriptionId: String? = null
)