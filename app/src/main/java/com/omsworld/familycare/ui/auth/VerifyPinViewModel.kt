package com.omsworld.familycare.ui.auth

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
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

    fun verify(mobile: String, otp: String) = viewModelScope.launch {
        setState(VerifyUiState.Loading)

        authRepo.verifyOtp(mobile, otp)
            .onSuccess { res ->
                if (res.status == "1" && res.userInfo != null) {
                    authRepo.saveSession(res)
                    setState(VerifyUiState.Success)
                } else {
                    setState(VerifyUiState.Error(res.message ?: "Invalid OTP"))
                }
            }
            .onError { msg, _ ->
                setState(VerifyUiState.Error(msg))
            }
    }

    fun resend(mobile: String) = viewModelScope.launch {
        authRepo.postRaw(UrlList.ResendOtp, mapOf("user_mob" to mobile))
            .onSuccess {
                try {
                    val obj = JSONObject(it)
                    val msg = obj.optString("message")
                    if (msg.isNotBlank()) showMessage(msg)
                } catch (_: Exception) { }
            }
            .onError { msg, _ -> showError(msg) }
    }
}