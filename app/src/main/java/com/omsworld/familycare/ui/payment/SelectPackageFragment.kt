package com.omsworld.familycare.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.viewModels
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.SelectpakageFrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SelectPackageFragment : BaseFragment<SelectpakageFrBinding>() {

    private val vm: SelectPackageViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        SelectpakageFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("SelectPackageFragment started")

        binding.btnPrev.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            val checkedId = binding.RDPakages.checkedRadioButtonId
            if (checkedId == -1) {
                snack("Please select a plan")
                return@setOnClickListener
            }
            val radio = binding.RDPakages.findViewById<RadioButton>(checkedId)
            toast("Selected: ${radio.text}")
            // TODO: navigate to payment gateway
        }

        binding.IVApplyPromo.setOnClickListener {
            val code = binding.ETPromocode.text.toString()
            if (code.isNotBlank()) toast("Promo code: $code")
        }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is PackageUiState.Loading -> Unit
                is PackageUiState.Success -> renderPackages(state.packages)
                is PackageUiState.Error -> snack(state.message)
            }
        }
    }

    private fun renderPackages(packages: List<com.omsworld.familycare.data.model.PackageModel>) {
        binding.RDPakages.removeAllViews()
        for ((index, pkg) in packages.withIndex()) {
            val radio = RadioButton(requireContext()).apply {
                id = View.generateViewId()
                text = pkg.PackageName
                tag = index
            }
            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 10
                bottomMargin = 10
            }
            binding.RDPakages.addView(radio, params)
        }
    }
}