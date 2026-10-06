package com.omsworld.familycare.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.NewsEventsFrBinding
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NewsEventsFragment : BaseFragment<NewsEventsFrBinding>() {

    private val vm: NewsEventsViewModel by viewModels()
    private lateinit var adapter: NewsEventsAdapter

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        NewsEventsFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("NewsEventsFragment started")

        adapter = NewsEventsAdapter(
            onLikeClick = { item -> vm.toggleLike(item) },
            onItemClick = { item -> openDetail(item) }
        )
        binding.RVNewsEventsList.layoutManager = LinearLayoutManager(requireContext())
        binding.RVNewsEventsList.adapter = adapter

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is NewsUiState.Loading -> binding.mprogressBar.visibility = View.VISIBLE
                is NewsUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE
                    adapter.submitList(state.items)
                }
                is NewsUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(state.message)
                }
            }
        }
    }

    private fun openDetail(item: com.omsworld.familycare.data.model.NewsEventsModel) {
        val bundle = Bundle().apply { putSerializable("newsEventsModel", item) }
        val fragment = NewsDetailFragment().apply { arguments = bundle }
        (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
            ?.replace(com.omsworld.familycare.R.id.fragment_container, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}