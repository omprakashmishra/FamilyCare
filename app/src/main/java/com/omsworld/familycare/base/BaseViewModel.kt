package com.omsworld.familycare.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel<S : Any> : ViewModel() {

    protected abstract val initialState: S

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _events = Channel<UiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    protected val currentState: S get() = _state.value

    protected fun setState(newState: S) { _state.value = newState }

    protected fun updateState(transform: (S) -> S) { _state.value = transform(_state.value) }

    protected fun sendEvent(event: UiEvent) {
        viewModelScope.launch { _events.send(event) }
    }

    protected fun showMessage(msg: String) = sendEvent(UiEvent.ShowMessage(msg))
    protected fun showError(msg: String) = sendEvent(UiEvent.ShowError(msg))
    protected fun navigateBack() = sendEvent(UiEvent.NavigateBack)

    protected fun handleError(t: Throwable, userMessage: String? = null) {
        Timber.e(t, "ViewModel error")
        sendEvent(UiEvent.ShowError(userMessage ?: t.message ?: "Something went wrong"))
    }
}