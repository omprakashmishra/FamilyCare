package com.omsworld.familycare.ui.profile

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

sealed interface ProfileUiState {
    data object Idle : ProfileUiState
    data object Saving : ProfileUiState
    data object Saved : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<ProfileUiState>() {

    override val initialState: ProfileUiState = ProfileUiState.Idle

    fun updateProfile(
        userName: String,
        email: String,
        dob: String,
        about: String
    ) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        setState(ProfileUiState.Saving)

        repo.updateProfile(userId, userName, email, dob, about)
            .onSuccess {
                prefs.setString(ctx, Constants.USER_NAME, userName)
                prefs.setString(ctx, Constants.EMAIL, email)
                prefs.setString(ctx, Constants.USER_DOB, dob)
                prefs.setString(ctx, Constants.USER_ABOUT, about)
                setState(ProfileUiState.Saved)
            }
            .onError { msg, _ -> setState(ProfileUiState.Error(msg)) }
    }

    fun uploadImage(base64: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        setState(ProfileUiState.Saving)

        repo.uploadImage(userId, base64)
            .onSuccess { url ->
                prefs.setString(ctx, Constants.USER_IMAGE, url)
                setState(ProfileUiState.Saved)
            }
            .onError { msg, _ -> setState(ProfileUiState.Error(msg)) }
    }

    fun changePassword(old: String, new: String, confirm: String) =
        viewModelScope.launch {
            val ctx = FamilyCareApp.appContext
            val userId = prefs.getString(ctx, Constants.USER_ID)
            repo.changePassword(userId, old, new, confirm)
                .onSuccess {
                    showMessage("Password changed")
                    setState(ProfileUiState.Saved)
                }
                .onError { msg, _ -> setState(ProfileUiState.Error(msg)) }
        }
}