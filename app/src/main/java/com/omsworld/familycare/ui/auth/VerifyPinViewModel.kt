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

sealed interface VerifyUiState {
    data object Idle : VerifyUiState
    data object Loading : VerifyUiState
    data object Success : VerifyUiState
    data class Error(val msg: String) : VerifyUiState
}

@HiltViewModel
class VerifyPinViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : BaseViewModel<VerifyUiState>() {

    override val initialState: VerifyUiState = VerifyUiState.Idle

    // ============================================================
    // VERIFY OTP
    // TODO: Migrate to Supabase Auth SMS OTP. For now, accept any 4-digit OTP
    // and let the user log in with their mobile.
    // ============================================================
    fun verify(mobile: String, otp: String) = viewModelScope.launch {
        if (otp.length < 4) {
            setState(VerifyUiState.Error("Enter a valid OTP"))
            return@launch
        }

        setState(VerifyUiState.Loading)

        // ─── Temporary flow: look up user by mobile and log them in ───
        // The real OTP check will come when we wire Supabase Auth.
        authRepo.loginByMobile(mobile, password = otp)
            .onSuccess {
                setState(VerifyUiState.Success)
            }
            .onError { msg, _ ->
                Timber.e("OTP login failed: $msg")
                setState(VerifyUiState.Error(msg))
            }
    }

    // ============================================================
    // RESEND OTP
    // TODO: Migrate to Supabase Auth SMS once available.
    // ============================================================
    fun resend(mobile: String) = viewModelScope.launch {
        // Supabase OTP resend not wired yet — show informational message.
        showError("Resend OTP is being migrated. Please try again later.")
    }
}