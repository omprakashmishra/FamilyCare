package com.omsworld.familycare.ui.contacts

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.core.result.safeApiCall
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.SearchModel
import com.omsworld.familycare.data.remote.SupabaseApiService
import com.omsworld.familycare.data.remote.dto.SupabasePhoneBookDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

sealed interface SharedContactsUiState {
    data object Loading : SharedContactsUiState
    data class Success(val contacts: List<SearchModel>) : SharedContactsUiState
    data class Error(val message: String) : SharedContactsUiState
}

@HiltViewModel
class SharedContactsViewModel @Inject constructor(
    @Named("supabase") private val supabase: SupabaseApiService,
    private val prefs: MySharedPreference
) : BaseViewModel<SharedContactsUiState>() {

    override val initialState: SharedContactsUiState = SharedContactsUiState.Loading

    init {
        loadContacts()
    }

    fun loadContacts() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)

        if (userId.isBlank()) {
            setState(SharedContactsUiState.Error("Not logged in"))
            return@launch
        }
        setState(SharedContactsUiState.Loading)

        safeApiCall {
            val mine = supabase.getPhoneBook(userEq = "eq.$userId")
            val familyContacts = if (familyId.isNotBlank()) {
                supabase.getFamilyPhoneBook(familyEq = "eq.$familyId")
                    .filter { it.userId != userId }
            } else emptyList()

            val all = mutableListOf<SearchModel>()
            mine.forEach { c ->
                all.add(
                    SearchModel(
                        id = c.id?.toString() ?: "",
                        name = c.name ?: "",
                        phone = c.phone ?: "",
                        comment = c.comment ?: "",
                        added_date = "",
                        added_by = "me"
                    )
                )
            }
            familyContacts.forEach { c ->
                all.add(
                    SearchModel(
                        id = c.id?.toString() ?: "",
                        name = c.name ?: "",
                        phone = c.phone ?: "",
                        comment = c.comment ?: "",
                        added_date = "",
                        added_by = c.addedByName ?: "family"
                    )
                )
            }
            all
        }
            .onSuccess { setState(SharedContactsUiState.Success(it)) }
            .onError { msg, _ -> setState(SharedContactsUiState.Error(msg)) }
    }

    fun addContact(name: String, phone: String, comment: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        val familyId = prefs.getString(ctx, Constants.FAMILY_ID)
        val userName = prefs.getString(ctx, Constants.USER_NAME)

        safeApiCall {
            val contact = SupabasePhoneBookDto(
                userId = userId,
                familyId = familyId.ifBlank { null },
                name = name,
                phone = phone.replace("\\s".toRegex(), ""),
                comment = comment,
                addedBy = userId,
                addedByName = userName
            )
            val response = supabase.addContact(contact)
            if (!response.isSuccessful) {
                throw IllegalStateException("Add contact failed: ${response.code()}")
            }
        }
            .onSuccess {
                showMessage("Contact added")
                loadContacts()
            }
            .onError { msg, _ -> showError(msg) }
    }

    fun deleteContact(contactId: String) = viewModelScope.launch {
        safeApiCall {
            val response = supabase.deleteContact(idEq = "eq.$contactId")
            if (!response.isSuccessful) {
                throw IllegalStateException("Delete failed: ${response.code()}")
            }
        }
            .onSuccess {
                showMessage("Contact deleted")
                loadContacts()
            }
            .onError { msg, _ -> showError(msg) }
    }
}
