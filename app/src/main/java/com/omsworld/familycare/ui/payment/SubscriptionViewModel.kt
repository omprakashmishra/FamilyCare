package com.omsworld.familycare.ui.payment

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SubscriptionUiState {
    data object Loading : SubscriptionUiState
    data class Loaded(
        val packageName: String,
        val amount: String,
        val startDate: String,
        val endDate: String,
        val activationCode: String,
        val paymentStatus: String
    ) : SubscriptionUiState
    data class Error(val message: String) : SubscriptionUiState
}

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repo: PaymentRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<SubscriptionUiState>() {

    override val initialState: SubscriptionUiState = SubscriptionUiState.Loading

    init {
        load()
    }

    fun load() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userName = prefs.getString(ctx, Constants.USER_NAME, "0")
        setState(SubscriptionUiState.Loading)
        repo.getUserInfo(userName)
            .onSuccess { root ->
                val data = root.optJSONObject("data") ?: return@onSuccess
                setState(
                    SubscriptionUiState.Loaded(
                        packageName = data.optString("PackageName"),
                        amount = data.optString("Amount"),
                        startDate = data.optString("StartDate"),
                        endDate = data.optString("EndDate"),
                        activationCode = data.optString("ActivationCode"),
                        paymentStatus = data.optString("PaymentStatusDescription")
                    )
                )
            }
            .onError { msg, _ -> setState(SubscriptionUiState.Error(msg)) }
    }

    fun resendMail() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        repo.resendMail(userId)
            .onSuccess { showMessage("Mail sent") }
            .onError { msg, _ -> showError(msg) }
    }
}