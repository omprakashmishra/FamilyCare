package com.omsworld.familycare.ui.shopping.web

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.model.ShoppingModel
import com.omsworld.familycare.data.remote.dto.SupabaseShoppingCategoryDto
import com.omsworld.familycare.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ShoppingSitesUiState {
    data object Loading : ShoppingSitesUiState
    data class Success(
        val sites: List<ShoppingModel>,
        val categories: List<SupabaseShoppingCategoryDto>
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
            .onSuccess { result ->
                setState(
                    ShoppingSitesUiState.Success(
                        sites = result.sites.map {
                            ShoppingModel(
                                id = it.id?.toString() ?: "",
                                title = it.name ?: "",
                                url = it.url ?: "",
                                image = it.image ?: ""
                            )
                        },
                        categories = result.categories
                    )
                )
            }
            .onError { msg, _ -> setState(ShoppingSitesUiState.Error(msg)) }
    }
}