package com.omsworld.familycare.ui.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.ShoppedHistoryFrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ShoppedHistoryFragment : BaseFragment<ShoppedHistoryFrBinding>() {

    private val vm: ShoppedHistoryViewModel by viewModels()
    private lateinit var adapter: ShoppedHistoryAdapter

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        ShoppedHistoryFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("ShoppedHistoryFragment started")

        adapter = ShoppedHistoryAdapter()
        binding.RVGroceryHistoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.RVGroceryHistoryList.adapter = adapter

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is ShoppedHistoryUiState.Loading ->
                    binding.mprogressBar.visibility = View.VISIBLE
                is ShoppedHistoryUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE
                    binding.TVTotalAmount.text = "Total Amount: ${state.totalAmount}"
                    adapter.submitList(state.items)
                }
                is ShoppedHistoryUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(state.message)
                }
            }
        }
    }
}