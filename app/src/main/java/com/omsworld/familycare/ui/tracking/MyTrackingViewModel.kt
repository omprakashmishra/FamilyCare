package com.omsworld.familycare.ui.tracking

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.repository.TrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
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
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        if (userId.isBlank()) {
            setState(TrackingUiState.Error("Not logged in"))
            return@launch
        }
        setState(TrackingUiState.Loading)
        repo.fetchFriends(userId)
            .onSuccess { list ->
                setState(TrackingUiState.Success(list.map { it.toTrackingItem() }))
            }
            .onError { msg, _ ->
                setState(TrackingUiState.Error(msg))
            }
    }

    private fun JSONObject.toTrackingItem(): TrackingItem = TrackingItem(
        id = optString("user_id"),
        name = optString("user_name"),
        mobile = optString("user_mob"),
        imageUrl = "http://108.170.54.215/App_development/Tracking/profileimage/" +
                optString("user_img"),
        lat = optString("lat").toDoubleOrNull() ?: 0.0,
        lng = optString("lng").toDoubleOrNull() ?: 0.0,
        address = optString("address"),
        battery = optString("battery", "0"),
        onlineStatus = optString("status", "Offline"),
        lastSeen = optString("time")
    )
}