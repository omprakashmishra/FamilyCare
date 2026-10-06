package com.omsworld.familycare.ui.contacts

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.UrlList
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.SearchModel
import com.omsworld.familycare.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

sealed interface SharedContactsUiState {
    data object Loading : SharedContactsUiState
    data class Success(val contacts: List<SearchModel>) : SharedContactsUiState
    data class Error(val message: String) : SharedContactsUiState
}

@HiltViewModel
class SharedContactsViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<SharedContactsUiState>() {

    override val initialState: SharedContactsUiState = SharedContactsUiState.Loading

    init {
        loadContacts()
    }

    fun loadContacts() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID, "0")
        if (userId.isBlank()) {
            setState(SharedContactsUiState.Error("Not logged in"))
            return@launch
        }
        setState(SharedContactsUiState.Loading)

        repo.postRaw(
            UrlList.phone_book_list,
            mapOf("user_id" to userId, "family_id" to familyId)
        ).onSuccess { raw ->
            try {
                val root = JSONObject(raw)
                val list = mutableListOf<SearchModel>()

                val own = root.optJSONArray("your_phone_book")
                if (own != null) {
                    for (i in 0 until own.length()) {
                        val o = own.optJSONObject(i) ?: continue
                        list.add(
                            SearchModel(
                                id = o.optString("id"),
                                name = o.optString("name"),
                                phone = o.optString("phone"),
                                comment = o.optString("comment"),
                                added_date = o.optString("added_date"),
                                added_by = "me"
                            )
                        )
                    }
                }

                val friends = root.optJSONArray("freinds_phone_book")
                if (friends != null) {
                    for (i in 0 until friends.length()) {
                        val o = friends.optJSONObject(i) ?: continue
                        val addedBy = o.optString("user_fullname")
                        val subs = o.optJSONArray("save_phone") ?: continue
                        for (j in 0 until subs.length()) {
                            val s = subs.optJSONObject(j) ?: continue
                            list.add(
                                SearchModel(
                                    name = s.optString("name"),
                                    phone = s.optString("phone"),
                                    comment = s.optString("comment"),
                                    added_date = s.optString("added_date"),
                                    added_by = addedBy
                                )
                            )
                        }
                    }
                }
                setState(SharedContactsUiState.Success(list))
            } catch (e: Exception) {
                setState(SharedContactsUiState.Error("Failed to parse contacts"))
            }
        }.onError { msg, _ -> setState(SharedContactsUiState.Error(msg)) }
    }

    fun addContact(name: String, phone: String, comment: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        repo.postRaw(
            UrlList.add_phone_book,
            mapOf(
                "user_id" to userId,
                "name" to name,
                "phone" to phone.replace("\\s".toRegex(), ""),
                "comment" to comment
            )
        ).onSuccess {
            showMessage("Contact added")
            loadContacts()
        }.onError { msg, _ -> showError(msg) }
    }

    fun deleteContact(phone: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        repo.postRaw(
            UrlList.delete_phone_book,
            mapOf("user_id" to userId, "phone" to phone)
        ).onSuccess {
            showMessage("Contact deleted")
            loadContacts()
        }.onError { msg, _ -> showError(msg) }
    }
}