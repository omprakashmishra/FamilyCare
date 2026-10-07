package com.omsworld.familycare.ui.chat

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.FriendListModel
import com.omsworld.familycare.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FriendsListUiState {
    data object Loading : FriendsListUiState
    data class Success(val friends: List<FriendListModel>) : FriendsListUiState
    data class Error(val message: String) : FriendsListUiState
}

@HiltViewModel
class FriendsListViewModel @Inject constructor(
    private val repo: ChatRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<FriendsListUiState>() {

    override val initialState: FriendsListUiState = FriendsListUiState.Loading

    init {
        loadFriends()
    }

    fun loadFriends() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        if (userId.isBlank()) {
            setState(FriendsListUiState.Error("Not logged in"))
            return@launch
        }
        setState(FriendsListUiState.Loading)

        repo.getChatList(userId)
            .onSuccess { items ->
                val friends = items.map { item ->
                    FriendListModel(
                        freind_id = item.friendId,
                        freind_fullname = item.friendFullName,
                        freind_img = item.friendImg,
                        friend_phone = item.friendPhone,
                        message = item.message,
                        message_id = item.messageId,
                        sender_id = item.senderId,
                        time = item.time,
                        type = item.type
                    )
                }
                setState(FriendsListUiState.Success(friends))
            }
            .onError { msg, _ -> setState(FriendsListUiState.Error(msg)) }
    }
}