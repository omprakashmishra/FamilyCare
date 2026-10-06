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
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        if (userId.isBlank()) {
            setState(FriendsListUiState.Error("Not logged in"))
            return@launch
        }
        setState(FriendsListUiState.Loading)
        repo.getChatList(userId)
            .onSuccess { root ->
                val list = mutableListOf<FriendListModel>()
                val arr = root.optJSONArray("chat_user_info")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        list.add(
                            FriendListModel(
                                freind_id = o.optString("freind_id"),
                                freind_fullname = o.optString("freind_fullname"),
                                freind_img = o.optString("freind_img"),
                                friend_phone = o.optString("friend_phone"),
                                message = o.optString("message"),
                                message_id = o.optString("message_id"),
                                sender_id = o.optString("sender_id"),
                                time = o.optString("time"),
                                type = o.optString("type")
                            )
                        )
                    }
                }
                setState(FriendsListUiState.Success(list))
            }
            .onError { msg, _ -> setState(FriendsListUiState.Error(msg)) }
    }
}