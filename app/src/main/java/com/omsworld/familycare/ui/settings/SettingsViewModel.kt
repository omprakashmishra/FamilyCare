package com.omsworld.familycare.ui.settings

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsUiState {
    data object Idle : SettingsUiState
    data class Loaded(val alarmMuted: Boolean, val friendsTracking: Boolean) : SettingsUiState
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: ProfileRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<SettingsUiState>() {

    override val initialState: SettingsUiState = SettingsUiState.Idle

    init { load() }

    fun load() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        setState(
            SettingsUiState.Loaded(
                alarmMuted = prefs.getString(ctx, Constants.ALARM_MUTE_STATUS, "0") == "1",
                friendsTracking = prefs.getString(ctx, Constants.ISFRIENDSTRACKING, "0") == "1"
            )
        )
    }

    fun setAlarmMuted(muted: Boolean) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.ASPNETUSERID, "0")
        val tracking = prefs.getString(context = ctx, key = Constants.ISFRIENDSTRACKING)
        val alarm = if (muted) "1" else "0"
        repo.alarmOnOff(userId, alarm, tracking)
            .onSuccess {
                prefs.setString(ctx, Constants.ALARM_MUTE_STATUS, alarm)
            }
            .onError { msg, _ -> showError(msg) }
    }

    fun setFriendsTracking(enabled: Boolean) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.ASPNETUSERID, "0")
        val alarm = prefs.getString(ctx, Constants.ALARM_MUTE_STATUS, "0")
        val tracking = if (enabled) "1" else "0"
        repo.alarmOnOff(userId, alarm, tracking)
            .onSuccess {
                prefs.setString(ctx, Constants.ISFRIENDSTRACKING, tracking)
            }
            .onError { msg, _ -> showError(msg) }
    }
}