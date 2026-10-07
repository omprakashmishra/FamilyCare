package com.omsworld.familycare.ui.family

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.JoinSafeJoinModel
import com.omsworld.familycare.data.remote.dto.SupabaseUserDto
import com.omsworld.familycare.data.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FamilyUiState {
    data object Loading : FamilyUiState
    data class Success(
        val familyName: String,
        val isAdmin: Boolean,
        val members: List<JoinSafeJoinModel>
    ) : FamilyUiState
    data class Error(val message: String) : FamilyUiState
}

@HiltViewModel
class FamilyMembersViewModel @Inject constructor(
    private val repo: FamilyRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<FamilyUiState>() {

    override val initialState: FamilyUiState = FamilyUiState.Loading

    init {
        loadFamily()
    }

    fun loadFamily() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        if (userId.isBlank()) {
            setState(FamilyUiState.Error("Not logged in"))
            return@launch
        }
        setState(FamilyUiState.Loading)

        repo.getFamilyGroupInfo(userId)
            .onSuccess { members ->
                // Read my own family info from prefs
                val familyName = prefs.getString(ctx, Constants.FAMILY_NAME)
                val isAdmin = prefs.getString(ctx, Constants.IsFamilyAdmin) == "1"

                setState(
                    FamilyUiState.Success(
                        familyName = familyName,
                        isAdmin = isAdmin,
                        members = members.map { it.toJoinSafeJoinModel(familyName) }
                    )
                )
            }
            .onError { msg, _ -> setState(FamilyUiState.Error(msg)) }
    }

    fun addMember(mobile: String, name: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)
        repo.addFamilyMember(userId, familyId, mobile)
            .onSuccess {
                showMessage("Invitation sent to $name")
                loadFamily()
            }
            .onError { msg, _ -> showError(msg) }
    }

    fun removeMember(memberMobile: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)
        repo.removeFamilyMember(userId, familyId, memberMobile)
            .onSuccess {
                showMessage("Member removed")
                loadFamily()
            }
            .onError { msg, _ -> showError(msg) }
    }
}

// ---- Mapping helper: SupabaseUserDto → JoinSafeJoinModel ----
private fun SupabaseUserDto.toJoinSafeJoinModel(familyName: String): JoinSafeJoinModel =
    JoinSafeJoinModel(
        user_id = aspnetUserId ?: "",
        user_name = userName ?: "",
        user_mobile = mobileNo ?: "",
        user_image = userImg ?: "",
        OnlineStatus = "Offline",
        address = "",
        time = "",
        request_type = "member",
        member_status = if (isFamilyAdmin == true) "Owner" else "Member",
        family_id = familyId ?: "",
        family_name = familyName
    )