package com.omsworld.familycare.ui.chat

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.ChatModel
import com.omsworld.familycare.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Success(val messages: List<ChatModel>) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<ChatUiState>() {

    override val initialState: ChatUiState = ChatUiState.Loading

    private var myUserId: String = ""

    fun init(friendId: String) {
        val ctx = FamilyCareApp.appContext
        myUserId = prefs.getString(ctx, Constants.USER_ID, "0")
        loadMessages(friendId)
    }

    fun loadMessages(friendId: String) = viewModelScope.launch {
        if (myUserId.isBlank()) return@launch
        setState(ChatUiState.Loading)

        repo.getChatDetails(myUserId, friendId)
            .onSuccess { root ->
                val status = root.optString("status")
                if (status != "1") {
                    setState(ChatUiState.Error("Unable to load messages"))
                    return@onSuccess
                }
                val list = mutableListOf<ChatModel>()
                val arr = root.optJSONArray("chat_info")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        list.add(
                            ChatModel(
                                sender_id = o.optString("sender_id"),
                                message = o.optString("message"),
                                message_id = o.optString("message_id"),
                                time = o.optString("time")
                            )
                        )
                    }
                }
                setState(ChatUiState.Success(list))
            }
            .onError { msg, _ -> setState(ChatUiState.Error(msg)) }
    }

    fun sendMessage(friendId: String, text: String) = viewModelScope.launch {
        if (myUserId.isBlank() || text.isBlank()) return@launch

        repo.sendChat(myUserId, friendId, text)
            .onSuccess {
                showMessage("Sent")
                loadMessages(friendId)
            }
            .onError { msg, _ -> showError(msg) }
    }

    fun getMyUserId(): String = myUserId
}