package com.omsworld.familycare.ui.tracking

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.repository.FamilyMemberWithLocation
import com.omsworld.familycare.data.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TrackingUiState {
    data object Loading : TrackingUiState
    data class Success(val friends: List<TrackingItem>) : TrackingUiState
    data class Error(val message: String) : TrackingUiState
}

@HiltViewModel
class MyTrackingViewModel @Inject constructor(
    private val repo: TrackingRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<TrackingUiState>() {

    override val initialState: TrackingUiState = TrackingUiState.Loading

    init {
        loadFriends()
    }

    fun loadFriends() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        if (userId.isBlank()) {
            setState(TrackingUiState.Error("Not logged in"))
            return@launch
        }
        setState(TrackingUiState.Loading)

        repo.fetchFriends(userId)
            .onSuccess { members ->
                setState(
                    TrackingUiState.Success(
                        members.map { it.toTrackingItem() }
                    )
                )
            }
            .onError { msg, _ ->
                setState(TrackingUiState.Error(msg))
            }
    }
}

private fun FamilyMemberWithLocation.toTrackingItem(): TrackingItem = TrackingItem(
    id = userId,
    name = userName,
    mobile = mobile,
    imageUrl = imageUrl,
    lat = lat.toDoubleOrNull() ?: 0.0,
    lng = lng.toDoubleOrNull() ?: 0.0,
    address = address,
    battery = battery,
    onlineStatus = onlineStatus,
    lastSeen = lastSeen
)