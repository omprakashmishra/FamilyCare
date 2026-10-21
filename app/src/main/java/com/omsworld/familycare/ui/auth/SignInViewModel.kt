package com.omsworld.familycare.ui.auth

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface SignInUiState {
    data object Idle : SignInUiState
    data object Loading : SignInUiState
    data object Success : SignInUiState
    data class NeedsOtp(val mobile: String) : SignInUiState
    data class Error(val msg: String) : SignInUiState
}

@HiltViewModel
class SignInUpViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : BaseViewModel<SignInUiState>() {

    override val initialState: SignInUiState = SignInUiState.Idle

    fun isAlreadyLoggedIn(): Boolean = authRepo.isLoggedIn()

    // ============================================================
    // LOGIN — Supabase ONLY (no PHP fallback)
    // ============================================================
    fun login(emailOrMobile: String, password: String) = viewModelScope.launch {
        if (emailOrMobile.isBlank()) {
            setState(SignInUiState.Error("Enter mobile number"))
            return@launch
        }
        setState(SignInUiState.Loading)

        authRepo.loginByMobile(emailOrMobile.trim(), password)
            .onSuccess {
                Timber.i("Login succeeded via Supabase")
                setState(SignInUiState.Success)
            }
            .onError { msg, _ ->
                Timber.e("Login failed: $msg")
                setState(SignInUiState.Error(msg))
            }
    }

    // ============================================================
    // FORGOT PASSWORD — will be migrated to Supabase next
    // ============================================================
    fun forgotPassword(mobile: String) = viewModelScope.launch {
        // TODO: migrate to Supabase
        setState(SignInUiState.Error("Forgot password is not yet enabled"))
    }
}