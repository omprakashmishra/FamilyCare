package com.omsworld.familycare.ui.splash

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashUiState {
    data object Idle : SplashUiState
    data object Ready : SplashUiState
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<SplashUiState>() {

    override val initialState: SplashUiState = SplashUiState.Idle

    init {
        viewModelScope.launch {
            setState(SplashUiState.Ready)
        }
    }

    fun isLoggedIn(): Boolean = authRepo.isLoggedIn()
}