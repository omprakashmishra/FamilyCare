package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.ApiService
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingRepository @Inject constructor(
    private val api: ApiService
) {

    suspend fun getGroceryList(userId: String): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.groceries_list,
            mapOf("user_id" to userId)
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun addGroceryItem(userId: String, note: String): ApiResult<JSONObject> =
        safeApiCall {
            val raw = api.post(
                UrlList.add_groceries_list,
                mapOf("user_id" to userId, "note" to note)
            ).body() ?: ""
            JSONObject(raw)
        }

    suspend fun deleteGroceryItem(userId: String, itemId: String): ApiResult<JSONObject> =
        safeApiCall {
            val raw = api.post(
                UrlList.delete_groceries_item,
                mapOf("user_id" to userId, "item_id" to itemId)
            ).body() ?: ""
            JSONObject(raw)
        }

    suspend fun addShoppedGroceries(
        userId: String,
        item: String,
        finalPrice: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.add_shopped_groceries,
            mapOf(
                "user_id" to userId,
                "item" to item,
                "price" to finalPrice
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun directPurchase(
        userId: String,
        item: String,
        finalPrice: String
    ): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.direct_purchase,
            mapOf(
                "user_id" to userId,
                "note" to item,
                "price" to finalPrice
            )
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun getShoppedHistory(userId: String): ApiResult<JSONObject> = safeApiCall {
        val raw = api.post(
            UrlList.shopped_groceries_history,
            mapOf("user_id" to userId, "UserID" to userId)
        ).body() ?: ""
        JSONObject(raw)
    }

    suspend fun getShoppingSites(categoryId: String): ApiResult<JSONObject> = safeApiCall {
        val url = if (categoryId == "YES") UrlList.shopping_category
        else UrlList.shopping_site_list
        val raw = api.post(url, mapOf("id" to categoryId)).body() ?: ""
        JSONObject(raw)
    }
}