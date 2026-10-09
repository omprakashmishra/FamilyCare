package com.omsworld.familycare.ui.shopping.web

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.omsworld.familycare.R
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.databinding.ShopingSitesFrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ShoppingSitesListActivity : BaseActivity<ShopingSitesFrBinding>() {

    private val vm: ShoppingSitesListViewModel by viewModels()

    private val sitesAdapter = ShoppingSitesAdapter { site ->
        val intent = Intent(this, ShopInWebActivity::class.java).apply {
            putExtra("siteName", site.title)
            putExtra("siteUrl", site.url)
        }
        startActivity(intent)
    }

    override fun inflateBinding(inflater: LayoutInflater) =
        ShopingSitesFrBinding.inflate(inflater)

    override fun onBindingReady() {
        Timber.d("ShoppingSitesListActivity started")

        binding.toolbarBack.TVToolbarText.visibility = View.VISIBLE
        binding.toolbarBack.tvback.visibility = View.GONE
        binding.toolbarBack.TVToolbarText.text = getString(R.string.shoping_sites)
        binding.toolbarBack.RLBackClick.setOnClickListener { finish() }

        binding.grdView.apply {
            layoutManager = GridLayoutManager(this@ShoppingSitesListActivity, 3)
            adapter = sitesAdapter
            setHasFixedSize(true)
        }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is ShoppingSitesUiState.Loading -> Unit

                is ShoppingSitesUiState.Success -> {
                    sitesAdapter.submitList(state.sites)

                    if (state.categories.isNotEmpty()) {
                        val categoryAdapter = HeadCategoryAdapter(
                            context = this,
                            categories = state.categories,
                            layoutId = com.omsworld.familycare.R.layout.head_category_item
                        ) { id -> vm.load(id) }

                        binding.RVCategories.layoutManager =
                            GridLayoutManager(this, 3)
                        binding.RVCategories.adapter = categoryAdapter
                    }
                }

                is ShoppingSitesUiState.Error -> snack(binding.root,state.message)
                else -> Unit
            }
        }
    }
}