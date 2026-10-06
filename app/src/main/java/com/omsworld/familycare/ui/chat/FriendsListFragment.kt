package com.omsworld.familycare.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.databinding.ChatListUserFrBinding
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FriendsListFragment : BaseFragment<ChatListUserFrBinding>() {

    private val vm: FriendsListViewModel by viewModels()
    private lateinit var adapter: FriendsListAdapter
    private var allFriends = listOf<com.omsworld.familycare.data.model.FriendListModel>()

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        ChatListUserFrBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("FriendsListFragment started")

        val userId = prefs.getString(requireContext(), Constants.USER_ID, "0")

        adapter = FriendsListAdapter(userId) { friend ->
            val bundle = Bundle().apply {
                putString("friend_Name", friend.freind_fullname)
                putString("friend_Img", friend.freind_img)
                putString("friend_Id", friend.freind_id)
                putString("friend_Phone", friend.friend_phone)
            }
            val fragment = ChatFragment().apply { arguments = bundle }
            (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()
                ?.replace(com.omsworld.familycare.R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.RVFriends.layoutManager = LinearLayoutManager(requireContext())
        binding.RVFriends.adapter = adapter

        binding.TVSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {
                filter(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        observeState()
    }

    private fun filter(query: String) {
        if (query.isBlank()) {
            adapter.submitList(allFriends)
            return
        }
        val q = query.lowercase()
        adapter.submitList(allFriends.filter { it.freind_fullname.lowercase().contains(q) })
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is FriendsListUiState.Loading -> binding.mprogressBar.visibility = View.VISIBLE
                is FriendsListUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE
                    allFriends = state.friends
                    adapter.submitList(allFriends)
                }
                is FriendsListUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(state.message)
                }
            }
        }
    }
}