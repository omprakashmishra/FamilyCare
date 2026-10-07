package com.omsworld.familycare.ui.auth

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.model.CountryCodeModel
import com.omsworld.familycare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

        // Unique user ID based on timestamp
        val userId = "user_${System.currentTimeMillis()}"

        authRepo.register(
            userId = userId,
            userName = username,
            email = email,
            mobile = mobile,
            password = password
        )
            .onSuccess {
                setState(SignUpUiState.Success(mobile))
            }
            .onError { msg, _ ->
                setState(SignUpUiState.Error(msg))
            }
    }
}
