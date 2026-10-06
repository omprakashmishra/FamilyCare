package com.omsworld.familycare.ui.diary

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface EditDiaryUiState {
    data object Idle : EditDiaryUiState
    data object Saving : EditDiaryUiState
    data object Saved : EditDiaryUiState
    data class Error(val message: String) : EditDiaryUiState
}

@HiltViewModel
class EditDiaryViewModel @Inject constructor(
    private val repo: DiaryRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<EditDiaryUiState>() {

    override val initialState: EditDiaryUiState = EditDiaryUiState.Idle

    fun save(noteId: String, subject: String, body: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        setState(EditDiaryUiState.Saving)

        val result = if (noteId.isBlank()) {
            repo.addDiary(userId, subject, body)
        } else {
            repo.editDiary(userId, noteId, subject, body)
        }
        result
            .onSuccess { setState(EditDiaryUiState.Saved) }
            .onError { msg, _ -> setState(EditDiaryUiState.Error(msg)) }
    }

    fun delete(noteId: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        repo.deleteDiary(userId, noteId)
            .onSuccess { setState(EditDiaryUiState.Saved) }
            .onError { msg, _ -> setState(EditDiaryUiState.Error(msg)) }
    }
}