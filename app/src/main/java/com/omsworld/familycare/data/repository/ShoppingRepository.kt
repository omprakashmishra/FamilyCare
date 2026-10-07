package com.omsworld.familycare.data.repository

import com.omsworld.familycare.core.result.ApiResult
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabaseGroceryDto
import com.omsworld.familycare.data.remote.dto.SupabaseShoppingCategoryDto
import com.omsworld.familycare.data.remote.dto.SupabaseShoppingSiteDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ShoppingRepository @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService
) {

    // ============================================================
    // GET GROCERIES (groceries_list.php)
    // ============================================================
    suspend fun getGroceryList(userId: String): ApiResult<List<SupabaseGroceryDto>> =
        safeApiCall {
            supabase.getGroceries(userEq = "eq.$userId")
        }

    // ============================================================
    // ADD GROCERY (add_groceries_list.php)
    // ============================================================
    suspend fun addGroceryItem(userId: String, note: String): ApiResult<Unit> =
        safeApiCall {
            val item = SupabaseGroceryDto(
                userId = userId,
                note = note,
                isShopped = "0",
                addedDate = nowIso()
            )
            val response = supabase.addGrocery(item)
            if (!response.isSuccessful) {
                throw IllegalStateException("Add grocery failed: ${response.code()}")
            }
        }

    // ============================================================
    // DELETE GROCERY (delete_groceries_item.php)
    // ============================================================
    suspend fun deleteGroceryItem(userId: String, itemId: String): ApiResult<Unit> =
        safeApiCall {
            val response = supabase.deleteGrocery(idEq = "eq.$itemId")
            if (!response.isSuccessful) {
                throw IllegalStateException("Delete grocery failed: ${response.code()}")
            }
        }

    // ============================================================
    // ADD SHOPPED GROCERIES (add_shopped_groceries.php)
    // Marks items as shopped and records price.
    // ============================================================
    suspend fun addShoppedGroceries(
        userId: String,
        item: String,
        finalPrice: String
    ): ApiResult<Unit> = safeApiCall {
        // item is a comma-separated list of item IDs
        val ids = item.split(",").map { it.trim() }.filter { it.isNotBlank() }
        for (id in ids) {
            val response = supabase.updateGrocery(
                idEq = "eq.$id",
                updates = mapOf("is_shopped" to "1", "price" to finalPrice)
            )
            if (!response.isSuccessful) {
                throw IllegalStateException("Update grocery failed: ${response.code()}")
            }
        }
    }

    // ============================================================
    // DIRECT PURCHASE (direct_purchase.php)
    // ============================================================
    suspend fun directPurchase(
        userId: String,
        item: String,
        finalPrice: String
    ): ApiResult<Unit> = safeApiCall {
        val entry = SupabaseGroceryDto(
            userId = userId,
            note = item,
            price = finalPrice,
            isShopped = "1",
            addedDate = nowIso()
        )
        val response = supabase.addGrocery(entry)
        if (!response.isSuccessful) {
            throw IllegalStateException("Direct purchase failed: ${response.code()}")
        }
    }

    // ============================================================
    // SHOPPED HISTORY (shopped_groceries_history.php)
    // ============================================================
    suspend fun getShoppedHistory(userId: String): ApiResult<List<SupabaseGroceryDto>> =
        safeApiCall {
            supabase.getGroceries(userEq = "eq.$userId", isShoppedEq = "eq.1")
        }

    // ============================================================
    // SHOPPING SITES (shopping_site_list.php / shopping_category.php)
    // ============================================================
    suspend fun getShoppingSites(categoryId: String): ApiResult<ShoppingListResult> =
        safeApiCall {
            if (categoryId == "YES") {
                // Categories
                val categories = supabase.getShoppingCategories()
                ShoppingListResult(sites = emptyList(), categories = categories)
            } else {
                // Sites in a category
                val sites = supabase.getShoppingSites(catEq = "eq.$categoryId")
                ShoppingListResult(sites = sites, categories = emptyList())
            }
        }

    private fun nowIso(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .format(Date())
}

data class ShoppingListResult(
    val sites: List<SupabaseShoppingSiteDto>,
    val categories: List<SupabaseShoppingCategoryDto>
)