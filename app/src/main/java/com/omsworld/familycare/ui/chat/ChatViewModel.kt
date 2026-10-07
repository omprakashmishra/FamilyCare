package com.omsworld.familycare.ui.chat

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.model.ChatModel
import com.omsworld.familycare.data.repository.ChatRepository
import com.omsworld.familycare.data.remote.SupabaseApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Success(val messages: List<ChatModel>) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository,
    @Named("supabase") private val supabase: SupabaseApiService
) : BaseViewModel<ChatUiState>() {

    override val initialState: ChatUiState = ChatUiState.Loading

    private var myUserId: String = ""
    private var currentFriendId: String = ""
    private var friendImg: String = ""

    fun init(myUserId: String, friendId: String) {
        this.myUserId = myUserId
        this.currentFriendId = friendId
        Timber.d("ChatViewModel.init: myUserId=$myUserId friendId=$friendId")
        loadFriendThenMessages(friendId)
    }

    private fun loadFriendThenMessages(friendId: String) = viewModelScope.launch {
        // 1. Fetch friend's profile image URL once
        try {
            val friends = supabase.getUserById(idEq = "eq.$friendId")
            friendImg = friends.firstOrNull()?.userImg ?: ""
            Timber.d("ChatViewModel: friendImg='$friendImg'")
        } catch (e: Exception) {
            Timber.e(e, "Failed to load friend profile")
        }

        // 2. Load messages
        loadMessages(friendId)
    }

    fun loadMessages(friendId: String = currentFriendId) = viewModelScope.launch {
        if (myUserId.isBlank()) {
            setState(ChatUiState.Error("Not logged in"))
            return@launch
        }
        if (friendId.isBlank()) {
            setState(ChatUiState.Error("Invalid chat partner"))
            return@launch
        }
        setState(ChatUiState.Loading)

        repo.getChatDetails(myUserId, friendId)
            .onSuccess { items ->
                val messages = items.map { item ->
                    ChatModel(
                        message_id = item.messageId,
                        message = item.message,
                        sender_id = item.senderId,
                        time = item.time,
                        type = item.type,
                        freind_id = item.friendId,
                        freind_fullname = "",
                        freind_img = friendImg    // ← NOW POPULATED
                    )
                }
                setState(ChatUiState.Success(messages))
            }
            .onError { msg, _ -> setState(ChatUiState.Error(msg)) }
    }

    fun sendMessage(friendId: String, text: String) = viewModelScope.launch {
        Timber.d("sendMessage: myUserId=$myUserId friendId=$friendId text=$text")

        if (myUserId.isBlank()) {
            showError("Not logged in")
            return@launch
        }
        if (friendId.isBlank()) {
            showError("Invalid chat partner")
            return@launch
        }
        if (text.isBlank()) return@launch

        repo.sendChat(myUserId, friendId, text)
            .onSuccess {
                Timber.d("sendChat: success")
                showMessage("Sent")
                loadMessages(friendId)
            }
            .onError { msg, _ ->
                Timber.e("sendChat: failed with $msg")
                showError(msg)
            }
    }

    fun getMyUserId(): String = myUserId
}