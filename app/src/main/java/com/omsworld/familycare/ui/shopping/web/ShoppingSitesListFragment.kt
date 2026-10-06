package com.omsworld.familycare.ui.shopping.web

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.data.model.ShoppingModel
import com.omsworld.familycare.databinding.ShopingSitesFrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ShoppingSitesListFragment : BaseFragment<ShopingSitesFrBinding>() {

    private val vm: ShoppingSitesListViewModel by viewModels()
    private val gridArray = ArrayList<ShoppingModel>()
    private lateinit var gridAdapter: ShoppingSitesAdapter

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        ShopingSitesFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("ShoppingSitesListFragment started")

        gridAdapter = ShoppingSitesAdapter(
            requireContext(),
            com.omsworld.familycare.R.layout.shopping_sites_row,
            gridArray
        )
        binding.grdView.adapter = gridAdapter

        binding.grdView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val site = gridArray[position]
            val intent = Intent(requireContext(), ShopInWebActivity::class.java).apply {
                putExtra("siteName", site.title)
                putExtra("siteUrl", site.url)
            }
            startActivity(intent)
        }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is ShoppingSitesUiState.Loading -> Unit
                is ShoppingSitesUiState.Success -> {
                    gridArray.clear()
                    gridArray.addAll(state.sites)
                    gridAdapter.notifyDataSetChanged()

                    state.categories?.let { cats ->
                        val adapter = HeadCategoryAdapter(
                            requireContext(),
                            cats,
                            com.omsworld.familycare.R.layout.head_category_item
                        ) { id -> vm.load(id) }
                        binding.RVCategories.layoutManager = GridLayoutManager(requireContext(), 3)
                        binding.RVCategories.adapter = adapter
                    }
                }
                is ShoppingSitesUiState.Error -> snack(state.message)
            }
        }
    }
}