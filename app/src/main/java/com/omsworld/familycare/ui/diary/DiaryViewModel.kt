package com.omsworld.familycare.ui.diary

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.DiaryModel
import com.omsworld.familycare.data.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DiaryUiState {
    data object Loading : DiaryUiState
    data class Success(val entries: List<DiaryModel>) : DiaryUiState
    data class Error(val message: String) : DiaryUiState
}

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repo: DiaryRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<DiaryUiState>() {

    override val initialState: DiaryUiState = DiaryUiState.Loading

    init {
        loadDiary()
    }

    fun loadDiary() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        if (userId.isBlank()) {
            setState(DiaryUiState.Error("Not logged in"))
            return@launch
        }
        setState(DiaryUiState.Loading)

        repo.getDiaryList(userId)
            .onSuccess { items ->
                val entries = items.map {
                    DiaryModel(
                        id = it.id?.toString() ?: "",
                        note = it.note ?: "",
                        subject = it.subject ?: "",
                        added_date = it.addedDate ?: ""
                    )
                }
                setState(DiaryUiState.Success(entries))
            }
            .onError { msg, _ -> setState(DiaryUiState.Error(msg)) }
    }

    fun deleteDiary(id: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID)
        repo.deleteDiary(userId, id)
            .onSuccess {
                showMessage("Note deleted")
                loadDiary()
            }
            .onError { msg, _ -> showError(msg) }
    }
}