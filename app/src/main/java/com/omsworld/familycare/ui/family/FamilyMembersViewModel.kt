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
import timber.log.Timber
import javax.inject.Inject

sealed interface FamilyUiState {
    data object Loading : FamilyUiState
    data class Success(
        val familyId: String,
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

    // ============================================================
    // LOAD FAMILY + MEMBERS
    // ============================================================
    fun loadFamily() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        if (userId.isBlank()) {
            setState(FamilyUiState.Error("Not logged in"))
            return@launch
        }
        setState(FamilyUiState.Loading)

        // 1. Fetch my own record to get latest family info
        repo.getFamilyGroupInfo(userId)
            .onSuccess { members ->
                // Re-read from prefs (AuthRepository.saveSession writes these)
                var familyId = prefs.getString(ctx, Constants.FAMILY_ID)
                var familyName = prefs.getString(ctx, Constants.FAMILY_NAME)
                var isAdmin = prefs.getString(ctx, Constants.IsFamilyAdmin) == "1"

                // If prefs are empty but we're in a family, sync from Supabase
                if (familyId.isBlank() && members.isNotEmpty()) {
                    // Try to find my record to get family_id
                    val me = members.firstOrNull { it.aspnetUserId == userId }
                    if (me != null && !me.familyId.isNullOrBlank()) {
                        familyId = me.familyId
                        familyName = me.familyName ?: ""
                        isAdmin = me.isFamilyAdmin == true

                        prefs.setString(ctx, Constants.FAMILY_ID, familyId)
                        prefs.setString(ctx, Constants.FAMILY_NAME, familyName)
                        prefs.setString(ctx, Constants.IsFamilyAdmin, if (isAdmin) "1" else "0")

                        Timber.d("Synced family from Supabase: id=$familyId name=$familyName admin=$isAdmin")
                    }
                }

                setState(
                    FamilyUiState.Success(
                        familyId = familyId,
                        familyName = familyName.ifBlank { "My Family" },
                        isAdmin = isAdmin,
                        members = members.map { it.toJoinSafeJoinModel(familyName) }
                    )
                )
            }
            .onError { msg, _ -> setState(FamilyUiState.Error(msg)) }
    }

    // ============================================================
    // CREATE OR UPDATE FAMILY
    // ============================================================
    fun createOrUpdateFamily(groupName: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        var familyId = prefs.getString(ctx, Constants.FAMILY_ID)

        if (userId.isBlank()) {
            showError("Not logged in")
            return@launch
        }
        if (groupName.isBlank()) {
            showError("Please enter a family name")
            return@launch
        }

        // Generate a unique family ID if creating for the first time
        if (familyId.isBlank()) {
            familyId = "FAM_${System.currentTimeMillis()}"
        }

        Timber.d("createOrUpdateFamily: id=$familyId name=$groupName")

        repo.createFamilyGroup(familyId, userId, groupName)
            .onSuccess {
                prefs.setString(ctx, Constants.FAMILY_ID, familyId)
                prefs.setString(ctx, Constants.FAMILY_NAME, groupName)
                prefs.setString(ctx, Constants.IsFamilyAdmin, "1")
                showMessage("Family saved")
                loadFamily()
            }
            .onError { msg, _ -> showError(msg) }
    }

    // ============================================================
    // ADD MEMBER
    // ============================================================
    fun addMember(mobile: String, name: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)

        if (familyId.isBlank()) {
            showError("Please create a family first")
            return@launch
        }
        if (mobile.length < 9) {
            showError("Enter a valid mobile number")
            return@launch
        }

        repo.addFamilyMember(userId, familyId, mobile)
            .onSuccess {
                showMessage("Invitation sent to $name")
                loadFamily()
            }
            .onError { msg, _ -> showError(msg) }
    }

    // ============================================================
    // REMOVE MEMBER
    // ============================================================
    fun removeMember(memberMobile: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)

        if (familyId.isBlank()) {
            showError("No family to remove from")
            return@launch
        }

        repo.removeFamilyMember(userId, familyId, memberMobile)
            .onSuccess {
                showMessage("Member removed")
                loadFamily()
            }
            .onError { msg, _ -> showError(msg) }
    }

    // ============================================================
    // LEAVE FAMILY (for non-admins)
    // ============================================================
    fun leaveFamily() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)

        if (familyId.isBlank()) {
            showError("No family to leave")
            return@launch
        }

        repo.leaveFamily(userId, familyId)
            .onSuccess {
                prefs.setString(ctx, Constants.FAMILY_ID, "")
                prefs.setString(ctx, Constants.FAMILY_NAME, "")
                prefs.setString(ctx, Constants.IsFamilyAdmin, "0")
                showMessage("Left family")
                loadFamily()
            }
            .onError { msg, _ -> showError(msg) }
    }
}

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