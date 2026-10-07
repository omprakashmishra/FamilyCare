package com.omsworld.familycare.ui.chat

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.omsworld.familycare.base.BaseActivity
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.databinding.CommentActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ChatActivity : BaseActivity<CommentActivityBinding>() {

    private val vm: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter

    private var friendName: String = ""
    private var friendImg: String = ""
    private var friendId: String = ""
    private var friendPhone: String = ""

    override fun inflateBinding(inflater: android.view.LayoutInflater) =
        CommentActivityBinding.inflate(inflater)

    override fun onBindingReady() {
        Timber.d("ChatActivity started")

        // ─── Read intent extras ───
        friendName = intent.getStringExtra("friend_Name") ?: ""
        friendImg = intent.getStringExtra("friend_Img") ?: ""
        friendId = intent.getStringExtra("friend_Id") ?: ""
        friendPhone = intent.getStringExtra("friend_Phone") ?: ""

        val myUserId = prefs.getString(this, Constants.USER_ID)
        Timber.d("ChatActivity: myUserId=$myUserId friendId=$friendId")

        // ─── Custom toolbar setup (using included toolbar_chat.xml) ───
        setupCustomToolbar()

        // ─── Initialize chat ───
        if (myUserId.isBlank() || friendId.isBlank()) {
            snack(binding.root, "Chat session error — please reopen")
            finish()
            return
        }
        vm.init(myUserId, friendId)

        // ─── Adapter setup ───
        adapter = ChatAdapter(
            myUserId = myUserId,
            myImage = prefs.getString(this, Constants.USER_IMAGE)
        )
        binding.commentsListView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.commentsListView.adapter = adapter

        // ─── Send button ───
        binding.sendComment.setOnClickListener {
            val text = binding.writeComment.text.toString().trim()
            if (text.isNotEmpty()) {
                binding.writeComment.setText("")
                vm.sendMessage(friendId, text)
            }
        }

        observeState()
    }

    /**
     * Sets up the custom toolbar. Uses the included toolbar_chat.xml.
     * Wire the back button and call button.
     */
    private fun setupCustomToolbar() {
        // Title
        binding.toolbarChat.TVToolbarText.text = friendName

        // Back button
        binding.toolbarChat.RLBackClick.setOnClickListener {
            onBackPressed()
        }

        // Call button (IV_side_one in toolbar_chat.xml)
        binding.toolbarChat.IVSideOne.visibility = View.VISIBLE
        binding.toolbarChat.IVSideOne.setOnClickListener {
            if (friendPhone.isNotBlank()) {
                startActivity(
                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:$friendPhone"))
                )
            } else {
                toast("No phone number available")
            }
        }
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is ChatUiState.Loading -> binding.mprogressBar.visibility = View.VISIBLE
                is ChatUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE
                    adapter.submitList(state.messages) {
                        if (state.messages.isNotEmpty()) {
                            binding.commentsListView.scrollToPosition(
                                state.messages.size - 1
                            )
                        }
                    }
                }
                is ChatUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(binding.root, state.message)
                }
                else -> Unit
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}