package com.omsworld.familycare.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroceryItemDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "added_by") val addedBy: String? = null,
    @Json(name = "added_by_name") val addedByName: String? = null,
    @Json(name = "note") val note: String? = null,
    @Json(name = "added_date") val addedDate: String? = null,
    @Json(name = "is_shopped") val isShopped: String? = null
)

@JsonClass(generateAdapter = true)
data class GroceryListDto(
    @Json(name = "added_date") val addedDate: String? = null,
    @Json(name = "added_item") val addedItems: List<GroceryItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class GroceryListResponse(
    @Json(name = "success") val success: String? = null,
    @Json(name = "shopping_note") val shoppingNote: List<GroceryListDto>? = null
)

@JsonClass(generateAdapter = true)
data class ShoppedHistoryResponse(
    @Json(name = "success") val success: String? = null,
    @Json(name = "shopped_note") val shoppedNote: List<ShoppedNoteDto>? = null
)

@JsonClass(generateAdapter = true)
data class ShoppedNoteDto(
    @Json(name = "added_item") val addedItems: List<ShoppedItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class ShoppedItemDto(
    @Json(name = "added_by_name") val addedByName: String? = null,
    @Json(name = "price") val price: String? = null,
    @Json(name = "added_date") val addedDate: String? = null,
    @Json(name = "item") val items: List<ShoppingItemDto>? = null
)

@JsonClass(generateAdapter = true)
data class ShoppingItemDto(
    @Json(name = "item_name") val itemName: String? = null
)

@JsonClass(generateAdapter = true)
data class ShoppingSiteDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "url") val url: String? = null,
    @Json(name = "image") val image: String? = null
)

@JsonClass(generateAdapter = true)
data class ShoppingSitesResponse(
    @Json(name = "success") val success: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "shopping_site_list") val shoppingSiteList: List<ShoppingSiteDto>? = null
)