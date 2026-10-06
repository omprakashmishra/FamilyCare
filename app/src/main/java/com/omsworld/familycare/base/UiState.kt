package com.omsworld.familycare.base

sealed interface UiEvent {
    data class ShowMessage(val message: String) : UiEvent
    data class ShowError(val message: String) : UiEvent
    data object NavigateBack : UiEvent
    data object HideKeyboard : UiEvent
    data class Navigate(
        val destination: String,
        val extras: Map<String, Any?> = emptyMap()
    ) : UiEvent
}

sealed interface UiState {
    data object Idle : UiState
    data object Loading : UiState
    data class Success<T>(val data: T) : UiState
    data class Error(val message: String) : UiState
}

sealed interface ListUiState<out T> {
    data object Loading : ListUiState<Nothing>
    data class Success<T>(val items: List<T>) : ListUiState<T>
    data class Error(val message: String) : ListUiState<Nothing>
    data object Empty : ListUiState<Nothing>
}