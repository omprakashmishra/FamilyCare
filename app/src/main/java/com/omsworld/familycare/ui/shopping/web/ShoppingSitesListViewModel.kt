package com.omsworld.familycare.ui.shopping.web

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.model.ShoppingModel
import com.omsworld.familycare.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

sealed interface ShoppingSitesUiState {
    data object Loading : ShoppingSitesUiState
    data class Success(
        val sites: List<ShoppingModel>,
        val categories: JSONArray?
    ) : ShoppingSitesUiState
    data class Error(val message: String) : ShoppingSitesUiState
}

@HiltViewModel
class ShoppingSitesListViewModel @Inject constructor(
    private val repo: ShoppingRepository
) : BaseViewModel<ShoppingSitesUiState>() {

    override val initialState: ShoppingSitesUiState = ShoppingSitesUiState.Loading

    init { load("YES") }

    fun load(categoryId: String) = viewModelScope.launch {
        setState(ShoppingSitesUiState.Loading)
        repo.getShoppingSites(categoryId)
            .onSuccess { root ->
                val status = root.optString("success")
                if (status != "1") {
                    setState(ShoppingSitesUiState.Error("Failed to load"))
                    return@onSuccess
                }
                val categories = root.optJSONArray("shopping_category")
                val list = mutableListOf<ShoppingModel>()
                val arr = root.optJSONArray("shopping_site_list")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        list.add(
                            ShoppingModel(
                                id = o.optString("id"),
                                title = o.optString("name"),
                                url = o.optString("url"),
                                image = o.optString("image")
                            )
                        )
                    }
                }
                setState(ShoppingSitesUiState.Success(list, categories))
            }
            .onError { msg, _ -> setState(ShoppingSitesUiState.Error(msg)) }
    }
}