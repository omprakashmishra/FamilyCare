package com.omsworld.familycare.ui.main

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MainUiState {
    data object Idle : MainUiState
    data object LoggedOut : MainUiState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : BaseViewModel<MainUiState>() {

    override val initialState: MainUiState = MainUiState.Idle

    fun logout() = viewModelScope.launch {
        authRepo.logout()
        setState(MainUiState.LoggedOut)
    }
}