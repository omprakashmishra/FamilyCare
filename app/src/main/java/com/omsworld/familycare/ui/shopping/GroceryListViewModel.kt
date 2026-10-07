package com.omsworld.familycare.ui.shopping

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.AddedItemModel
import com.omsworld.familycare.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface GroceryUiState {
    data object Loading : GroceryUiState
    data class Success(val items: List<AddedItemModel>) : GroceryUiState
    data class Error(val message: String) : GroceryUiState
}

@HiltViewModel
class GroceryListViewModel @Inject constructor(
    private val repo: ShoppingRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<GroceryUiState>() {

    override val initialState: GroceryUiState = GroceryUiState.Loading

    init { load() }

    fun load() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        if (userId.isBlank()) {
            setState(GroceryUiState.Error("Not logged in"))
            return@launch
        }
        setState(GroceryUiState.Loading)

        repo.getGroceryList(userId)
            .onSuccess { items ->
                setState(
                    GroceryUiState.Success(
                        items.map {
                            AddedItemModel(
                                id = it.id?.toString() ?: "",
                                added_by = it.addedBy ?: "",
                                added_by_name = it.addedByName ?: "",
                                note = it.note ?: "",
                                added_date = it.addedDate ?: "",
                                isItemShopped = it.isShopped ?: "0"
                            )
                        }
                    )
                )
            }
            .onError { msg, _ -> setState(GroceryUiState.Error(msg)) }
    }

    fun addItem(note: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        repo.addGroceryItem(userId, note)
            .onSuccess { load() }
            .onError { msg, _ -> showError(msg) }
    }

    fun deleteItem(itemId: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        repo.deleteGroceryItem(userId, itemId)
            .onSuccess { load() }
            .onError { msg, _ -> showError(msg) }
    }
}