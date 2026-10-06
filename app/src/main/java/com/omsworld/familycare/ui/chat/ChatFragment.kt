package com.omsworld.familycare.ui.chat

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.base.BaseFragment
import com.omsworld.familycare.databinding.CommentActivityBinding
import com.omsworld.familycare.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ChatFragment : BaseFragment<CommentActivityBinding>() {

    private val vm: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    private var friendName: String = ""
    private var friendImg: String = ""
    private var friendId: String = ""
    private var friendPhone: String = ""

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        CommentActivityBinding.inflate(inflater, container, false)

    override fun onBindingReady() {
        Timber.d("ChatFragment started")

        arguments?.let {
            friendName = it.getString("friend_Name", "")
            friendImg = it.getString("friend_Img", "")
            friendId = it.getString("friend_Id", "")
            friendPhone = it.getString("friend_Phone", "")
        }

        (activity as? MainActivity)?.findViewById<androidx.appcompat.widget.Toolbar>(
            com.omsworld.familycare.R.id.toolbar12
        )?.title = friendName

        vm.init(friendId)

        adapter = ChatAdapter(
            myUserId = vm.getMyUserId(),
            myImage = prefs.getString(
                requireContext(),
                com.omsworld.familycare.core.Constants.USER_IMAGE,
                "0"
            )
        )
        binding.commentsListView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.commentsListView.adapter = adapter

        binding.sendComment.setOnClickListener {
            val text = binding.writeComment.text.toString().trim()
            if (text.isNotEmpty()) {
                binding.writeComment.setText("")
                vm.sendMessage(friendId, text)
            }
        }

        binding.IVSideOne.setOnClickListener {
            if (friendPhone.isNotBlank()) {
                startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$friendPhone")))
            }
        }

        observeState()
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is ChatUiState.Loading -> binding.mprogressBar.visibility = View.VISIBLE
                is ChatUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE
                    adapter.submitList(state.messages) {
                        if (state.messages.isNotEmpty()) {
                            binding.commentsListView.scrollToPosition(state.messages.size - 1)
                        }
                    }
                }
                is ChatUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(state.message)
                }
            }
        }
    }
}