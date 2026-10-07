package com.omsworld.familycare.ui.family

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.JoinSafeJoinModel
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabaseUserDto
import com.omsworld.familycare.data.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

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
    private val prefs: MySharedPreference,
    @Named("supabase") private val supabase: SupabaseApiService
) : BaseViewModel<FamilyUiState>() {

    override val initialState: FamilyUiState = FamilyUiState.Loading

    init {
        loadFamily()
    }

    fun loadFamily() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        val myMobile = prefs.getString(ctx, Constants.MOBILE_only)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)
        val familyName = prefs.getString(ctx, Constants.FAMILY_NAME)
        val isAdmin = prefs.getString(ctx, Constants.IsFamilyAdmin) == "1"

        Timber.d("loadFamily: userId=$userId myMobile=$myMobile familyId=$familyId")

        if (userId.isBlank()) {
            setState(FamilyUiState.Error("Not logged in"))
            return@launch
        }
        setState(FamilyUiState.Loading)

        repo.getFamilyGroupInfo(userId)
            .onSuccess { members ->
                val memberModels = members.map { it.toJoinSafeJoinModel(familyName) }
                Timber.d("loadFamily: found ${memberModels.size} members")

                // Fetch pending join requests
                val requestModels = if (myMobile.isNotBlank()) {
                    fetchJoinRequests(myMobile, familyName, familyId, memberModels)
                } else emptyList()

                setState(
                    FamilyUiState.Success(
                        familyId = familyId,
                        familyName = familyName.ifBlank { "My Family" },
                        isAdmin = isAdmin,
                        members = memberModels + requestModels
                    )
                )
            }
            .onError { msg, _ ->
                Timber.e("loadFamily error: $msg")
                setState(FamilyUiState.Error(msg))
            }
    }

    private suspend fun fetchJoinRequests(
        myMobile: String,
        familyName: String,
        familyId: String,
        existingMembers: List<JoinSafeJoinModel>
    ): List<JoinSafeJoinModel> {
        return try {
            val requests = supabase.getRequestsForPhone(toPhoneEq = "eq.$myMobile")
            Timber.d("fetchJoinRequests: found ${requests.size} pending requests")

            val existingMobiles = existingMembers.map { it.user_mobile }.toSet()

            requests
                .filter { !existingMobiles.contains(it.fromPhone) }
                .mapNotNull { req ->
                    val requester = try {
                        supabase.getUserByMobile(mobileEq = "eq.${req.fromPhone}")
                            .firstOrNull()
                    } catch (e: Exception) { null }

                    JoinSafeJoinModel(
                        user_id = requester?.aspnetUserId ?: "",
                        user_name = requester?.userName ?: "Unknown",
                        user_mobile = requester?.mobileNo ?: req.fromPhone,
                        user_image = requester?.userImg ?: "",
                        OnlineStatus = "Offline",
                        address = "",
                        time = req.createdAt ?: "",
                        request_type = "freind_request",
                        member_status = "Request",
                        family_id = req.familyId ?: familyId,
                        family_name = familyName
                    )
                }
        } catch (e: Exception) {
            Timber.e(e, "Failed to fetch join requests")
            emptyList()
        }
    }

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

        if (familyId.isBlank()) {
            familyId = "FAM_${System.currentTimeMillis()}"
        }

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

    fun addMember(mobile: String, name: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)

        if (familyId.isBlank()) {
            showError("Please create a family first")
            return@launch
        }

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

    fun acceptJoinRequest(requester: JoinSafeJoinModel) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val myUserId = prefs.getString(ctx, Constants.USER_ID)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)

        if (familyId.isBlank()) {
            showError("Create a family first")
            return@launch
        }

        // 1. Add requester to family
        repo.addFamilyMember(myUserId, familyId, requester.user_mobile)
            .onSuccess {
                // 2. Mark request as accepted
                markRequestHandled(requester.user_mobile, accepted = true)
                showMessage("${requester.user_name} joined your family")
                loadFamily()
            }
            .onError { msg, _ -> showError(msg) }
    }

    fun declineJoinRequest(requester: JoinSafeJoinModel) = viewModelScope.launch {
        markRequestHandled(requester.user_mobile, accepted = false)
        showMessage("Request declined")
        loadFamily()
    }

    private suspend fun markRequestHandled(fromPhone: String, accepted: Boolean) {
        try {
            val myMobile = prefs.getString(FamilyCareApp.appContext, Constants.MOBILE_only)
            val requests = supabase.getRequestsForPhone(toPhoneEq = "eq.$myMobile")
            val target = requests.firstOrNull { it.fromPhone == fromPhone }
            if (target?.id != null) {
                val updates = if (accepted) mapOf("is_accepted" to "1")
                else mapOf("is_declined" to "1")
                supabase.updateFamilyRequest(idEq = "eq.${target.id}", updates = updates)
                Timber.d("markRequestHandled: request ${target.id} -> $updates")
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to mark request")
        }
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