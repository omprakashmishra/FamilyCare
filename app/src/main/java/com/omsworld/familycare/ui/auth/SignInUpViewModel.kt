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

    fun login(emailOrMobile: String, password: String) = viewModelScope.launch {
        if (emailOrMobile.isBlank()) {
            setState(SignInUiState.Error("Enter mobile number"))
            return@launch
        }
        setState(SignInUiState.Loading)

        authRepo.postRaw(
            UrlList.LOG_IN,
            mapOf("email" to emailOrMobile, "password" to password)
        ).onSuccess { raw ->
            try {
                val obj = JSONObject(raw)
                val status = obj.optString("status")
                val message = obj.optString("message")

                when (status) {
                    "0" -> setState(SignInUiState.Error(message.ifBlank { "Login failed" }))
                    "1" -> {
                        val userInfo = obj.optJSONObject("user_info")
                        if (userInfo != null) {
                            authRepo.persistUserInfo(userInfo)
                        }
                        setState(SignInUiState.Success)
                    }
                    "2" -> {
                        val mobile = obj.optString("mobile")
                        setState(SignInUiState.NeedsOtp(mobile))
                    }
                    else -> setState(SignInUiState.Error(message.ifBlank { "Unknown response" }))
                }
            } catch (e: Exception) {
                setState(SignInUiState.Error("Invalid response"))
            }
        }.onError { msg, _ ->
            setState(SignInUiState.Error(msg))
        }
    }

    fun forgotPassword(mobile: String) = viewModelScope.launch {
        authRepo.sendForgotPassword(mobile)
            .onSuccess { showMessage("Please check your mobile") }
            .onError { msg, _ -> showError(msg) }
    }
}