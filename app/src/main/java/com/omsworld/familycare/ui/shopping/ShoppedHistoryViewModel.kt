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
        val userId = prefs.getString(ctx, Constants.USER_ID)
        if (userId.isBlank()) {
            setState(ShoppedHistoryUiState.Error("Not logged in"))
            return@launch
        }
        setState(ShoppedHistoryUiState.Loading)

        repo.getShoppedHistory(userId)
            .onSuccess { items ->
                var total = 0.0
                val history = items.map {
                    val price = it.price ?: "0"
                    total += price.toDoubleOrNull() ?: 0.0
                    ShoppedHistoryModel(
                        item_name = it.note ?: "",
                        item_count = "1",
                        added_by_name = it.addedByName ?: "",
                        added_date = it.addedDate ?: "",
                        price = price
                    )
                }
                setState(ShoppedHistoryUiState.Success(history, total))
            }
            .onError { msg, _ -> setState(ShoppedHistoryUiState.Error(msg)) }
    }
}