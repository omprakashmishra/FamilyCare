package com.omsworld.familycare.ui.shopping

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.ShoppedHistoryModel
import com.omsworld.familycare.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ShoppedHistoryUiState {
    data object Loading : ShoppedHistoryUiState
    data class Success(
        val items: List<ShoppedHistoryModel>,
        val totalAmount: Double
    ) : ShoppedHistoryUiState
    data class Error(val message: String) : ShoppedHistoryUiState
}

@HiltViewModel
class ShoppedHistoryViewModel @Inject constructor(
    private val repo: ShoppingRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<ShoppedHistoryUiState>() {

    override val initialState: ShoppedHistoryUiState = ShoppedHistoryUiState.Loading

    init { load() }

    fun load() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        if (userId.isBlank()) {
            setState(ShoppedHistoryUiState.Error("Not logged in"))
            return@launch
        }
        setState(ShoppedHistoryUiState.Loading)
        repo.getShoppedHistory(userId)
            .onSuccess { root ->
                val list = mutableListOf<ShoppedHistoryModel>()
                var total = 0.0
                val arr = root.optJSONArray("shopped_note")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val day = arr.optJSONObject(i) ?: continue
                        val sub = day.optJSONArray("added_item") ?: continue
                        for (j in 0 until sub.length()) {
                            val o = sub.optJSONObject(j) ?: continue
                            val price = o.optString("price")
                            total += price.toDoubleOrNull() ?: 0.0

                            val itemsArr = o.optJSONArray("item")
                            val itemName = buildString {
                                if (itemsArr != null) {
                                    for (k in 0 until itemsArr.length()) {
                                        val it = itemsArr.optJSONObject(k) ?: continue
                                        append("$k.  ${it.optString("item_name")}\n")
                                    }
                                }
                            }
                            list.add(
                                ShoppedHistoryModel(
                                    item_name = itemName,
                                    item_count = (itemsArr?.length() ?: 0).toString(),
                                    added_by_name = o.optString("added_by_name"),
                                    added_date = o.optString("added_date"),
                                    price = price
                                )
                            )
                        }
                    }
                }
                setState(ShoppedHistoryUiState.Success(list, total))
            }
            .onError { msg, _ -> setState(ShoppedHistoryUiState.Error(msg)) }
    }
}