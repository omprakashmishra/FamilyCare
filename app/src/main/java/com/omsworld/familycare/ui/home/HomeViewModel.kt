package com.omsworld.familycare.ui.home

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.data.local.MySharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Idle : HomeUiState
    data class Loaded(
        val name: String,
        val mobile: String,
        val image: String
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefs: MySharedPreference
) : BaseViewModel<HomeUiState>() {

    override val initialState: HomeUiState = HomeUiState.Idle

    init {
        loadUser()
    }

    private fun loadUser() = viewModelScope.launch {
        try {
            val ctx = FamilyCareApp.appContext
            setState(
                HomeUiState.Loaded(
                    name = prefs.getString(ctx, Constants.USER_NAME, "0"),
                    mobile = prefs.getString(ctx, Constants.MOBILE_only, "0"),
                    image = prefs.getString(ctx, Constants.USER_IMAGE, "0")
                )
            )
        } catch (e: Exception) {
            setState(HomeUiState.Error("Failed to load profile"))
        }
    }
}