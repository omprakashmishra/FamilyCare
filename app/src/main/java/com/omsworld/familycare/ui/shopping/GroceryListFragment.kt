package com.omsworld.familycare.ui.shopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.databinding.GroceryListFrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class GroceryListFragment : BaseFragment<GroceryListFrBinding>() {

    companion object {
        val ShopedItem = mutableListOf<String>()
        fun shoppedItem() {}
    }

    private val vm: GroceryListViewModel by viewModels()
    private lateinit var adapter: GroceryListAdapter

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        GroceryListFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("GroceryListFragment started")

        val userId = prefs.getString(requireContext(), Constants.USER_ID, "0")

        adapter = GroceryListAdapter(
            userId = userId,
            onDeleteClick = { item -> vm.deleteItem(item.id) },
            onCheckedChange = { }
        )
        binding.RVGroceryList.layoutManager = LinearLayoutManager(requireContext())
        binding.RVGroceryList.adapter = adapter

        binding.TVAddGrocery.setOnClickListener {
            val text = binding.body.text.toString().trim()
            if (text.isNotEmpty()) {
                vm.addItem(text)
                binding.body.setText("")
            }
        }

        binding.IVBarcode.setOnClickListener {
            startActivity(android.content.Intent(
                requireContext(), ScanActivity::class.java
            ))
        }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is GroceryUiState.Loading -> binding.mprogressBar.visibility = View.VISIBLE
                is GroceryUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE
                    adapter.submitList(state.items)
                }
                is GroceryUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(state.message)
                }
            }
        }
    }
}