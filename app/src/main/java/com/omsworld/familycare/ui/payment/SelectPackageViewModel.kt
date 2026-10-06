package com.omsworld.familycare.ui.payment

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.PackageModel
import com.omsworld.familycare.data.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PackageUiState {
    data object Loading : PackageUiState
    data class Success(val packages: List<PackageModel>) : PackageUiState
    data class Error(val message: String) : PackageUiState
}

@HiltViewModel
class SelectPackageViewModel @Inject constructor(
    private val repo: PaymentRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<PackageUiState>() {

    override val initialState: PackageUiState = PackageUiState.Loading

    init {
        loadPackages()
    }

    fun loadPackages() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val countryCode = prefs.getString(ctx, Constants.COUNTYCD, "0")
        val userName = prefs.getString(ctx, Constants.USER_NAME, "0")

        setState(PackageUiState.Loading)
        repo.getPackages(countryCode, userName)
            .onSuccess { root ->
                val list = mutableListOf<PackageModel>()
                val response = root.optJSONObject("Response") ?: root
                val arr = response.optJSONArray("Data")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        list.add(
                            PackageModel(
                                PackageID = o.optString("PackageID"),
                                PackageName = o.optString("PackageName"),
                                Amount = o.optString("Amount"),
                                Type = o.optString("AmountType"),
                                setPackageDesc = o.optString("PackageDesc")
                            )
                        )
                    }
                }
                setState(PackageUiState.Success(list))
            }
            .onError { msg, _ -> setState(PackageUiState.Error(msg)) }
    }

    fun processPayment(
        packageId: String,
        promoCode: String,
        amount: String,
        referralCode: String
    ) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        val deviceId = android.provider.Settings.Secure.getString(
            ctx.contentResolver, android.provider.Settings.Secure.ANDROID_ID
        )
        repo.processPayment(userId, packageId, promoCode, deviceId, amount, referralCode)
            .onSuccess { showMessage("Payment initiated") }
            .onError { msg, _ -> showError(msg) }
    }
}