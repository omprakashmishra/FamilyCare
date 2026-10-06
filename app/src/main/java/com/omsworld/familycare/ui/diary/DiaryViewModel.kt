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
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        if (userId.isBlank()) {
            setState(DiaryUiState.Error("Not logged in"))
            return@launch
        }
        setState(DiaryUiState.Loading)
        repo.getDiaryList(userId)
            .onSuccess { root ->
                val list = mutableListOf<DiaryModel>()
                val arr = root.optJSONArray("my_diary")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        list.add(
                            DiaryModel(
                                id = o.optString("id"),
                                note = o.optString("note"),
                                subject = o.optString("subject"),
                                added_date = o.optString("added_date")
                            )
                        )
                    }
                }
                setState(DiaryUiState.Success(list))
            }
            .onError { msg, _ -> setState(DiaryUiState.Error(msg)) }
    }

    fun deleteDiary(id: String) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        repo.deleteDiary(userId, id)
            .onSuccess {
                showMessage("Note deleted")
                loadDiary()
            }
            .onError { msg, _ -> showError(msg) }
    }
}