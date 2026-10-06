package com.omsworld.familycare.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.CocUserPackageActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment<CocUserPackageActivityBinding>() {

    private val vm: SubscriptionViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        CocUserPackageActivityBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("SubscriptionFragment started")

        binding.BtRenewSubscription.setOnClickListener {
            val fragment = SelectPackageFragment()
            parentFragmentManager.beginTransaction()
                .replace(com.omsworld.familycare.R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is SubscriptionUiState.Loading -> Unit
                is SubscriptionUiState.Loaded -> {
                    binding.tvPackageName.text = state.packageName
                    binding.tvPackageamount.text = state.amount
                    binding.tvStartDateValue.text = state.startDate
                    binding.tvEndDateValue.text = state.endDate
                    binding.tvRenewDateValue.text = state.activationCode
                    binding.tvPaymentStatusDescription.text = state.paymentStatus
                    binding.LLProfileUI.visibility = View.VISIBLE
                }
                is SubscriptionUiState.Error -> snack(state.message)
            }
        }
    }
}