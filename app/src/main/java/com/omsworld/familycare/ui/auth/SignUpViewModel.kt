package com.omsworld.familycare.ui.auth

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.model.CountryCodeModel
import com.omsworld.familycare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data class Success(val mobile: String) : SignUpUiState
    data class Error(val msg: String) : SignUpUiState
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepo: AuthRepository
) : BaseViewModel<SignUpUiState>() {

    override val initialState: SignUpUiState = SignUpUiState.Idle

    private val countries = mutableListOf<CountryCodeModel>()

    fun setCountryCode(code: String) { /* store for later use */ }
    fun getCountry(pos: Int): CountryCodeModel? = countries.getOrNull(pos)

    fun register(
        username: String,
        email: String,
        mobile: String,
        password: String,
        invite: String
    ) = viewModelScope.launch {
        setState(SignUpUiState.Loading)

        authRepo.postRaw(
            UrlList.reg_with_mob,
            mapOf(
                "UserName" to username,
                "Email" to email,
                "MobileNo" to mobile,
                "Password" to password
            )
        ).onSuccess { raw ->
            try {
                val obj = JSONObject(raw)
                val status = obj.optString("status")
                val message = obj.optString("message")
                if (status == "1") {
                    setState(SignUpUiState.Success(mobile))
                } else {
                    setState(SignUpUiState.Error(message.ifBlank { "Registration failed" }))
                }
            } catch (e: Exception) {
                setState(SignUpUiState.Error("Invalid response"))
            }
        }.onError { msg, _ ->
            setState(SignUpUiState.Error(msg))
        }
    }
}