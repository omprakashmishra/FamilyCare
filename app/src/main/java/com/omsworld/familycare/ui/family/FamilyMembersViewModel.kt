package com.omsworld.familycare.ui.family

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.JoinSafeJoinModel
import com.omsworld.familycare.data.repository.FamilyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
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
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        if (userId.isBlank()) {
            setState(FamilyUiState.Error("Not logged in"))
            return@launch
        }
        setState(FamilyUiState.Loading)
        repo.getFamilyGroupInfo(userId)
            .onSuccess { root ->
                val familyId = root.optString("family_id")
                val familyName = root.optString("family_name")
                val isAdmin = root.optString("isFamilyAdmin") == "1"

                prefs.setString(ctx, Constants.FAMILY_ID, familyId)
                prefs.setString(ctx, Constants.FAMILY_NAME, familyName)
                prefs.setString(ctx, Constants.IsFamilyAdmin, if (isAdmin) "1" else "0")

                val members = parseMembers(root)
                setState(FamilyUiState.Success(familyName, isAdmin, members))
            }
            .onError { msg, _ ->
                setState(FamilyUiState.Error(msg))
            }
    }

    fun addMember(mobile: String, name: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID, "0")
        repo.addFamilyMember(userId, familyId, mobile)
            .onSuccess {
                showMessage("Invitation sent to $name")
                loadFamily()
            }
            .onError { msg, _ -> showError(msg) }
    }

    fun removeMember(memberMobile: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID, "0")
        repo.removeFamilyMember(userId, familyId, memberMobile)
            .onSuccess {
                showMessage("Member removed")
                loadFamily()
            }
            .onError { msg, _ -> showError(msg) }
    }

    private fun parseMembers(root: JSONObject): List<JoinSafeJoinModel> {
        val list = mutableListOf<JoinSafeJoinModel>()
        val friends = root.optJSONArray("freind_list")
        if (friends != null) {
            for (i in 0 until friends.length()) {
                val o = friends.optJSONObject(i) ?: continue
                list.add(
                    JoinSafeJoinModel(
                        user_id = o.optString("user_id"),
                        user_name = o.optString("user_name"),
                        user_mobile = o.optString("user_mob"),
                        user_image = o.optString("user_img"),
                        OnlineStatus = o.optString("status"),
                        address = o.optString("address"),
                        time = o.optString("time"),
                        request_type = o.optString("type"),
                        member_status = o.optString("member_status"),
                        family_id = root.optString("family_id"),
                        family_name = root.optString("family_name")
                    )
                )
            }
        }
        val requests = root.optJSONArray("request_list")
        if (requests != null) {
            for (i in 0 until requests.length()) {
                val o = requests.optJSONObject(i) ?: continue
                list.add(
                    JoinSafeJoinModel(
                        user_id = o.optString("user_id"),
                        user_name = o.optString("user_name"),
                        user_mobile = o.optString("user_mobile"),
                        user_image = o.optString("user_img"),
                        OnlineStatus = o.optString("status"),
                        address = o.optString("address"),
                        time = o.optString("time"),
                        request_type = "friend_request",
                        member_status = o.optString("member_status"),
                        family_id = o.optString("family_id"),
                        family_name = o.optString("family_name")
                    )
                )
            }
        }
        return list
    }
}