package com.omsworld.familycare.ui.news

import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.data.model.NewsEventsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface NewsDetailUiState {
    data class Loaded(val item: NewsEventsModel) : NewsDetailUiState
}

@HiltViewModel
class NewsDetailViewModel @Inject constructor() : BaseViewModel<NewsDetailUiState>() {

    override val initialState: NewsDetailUiState =
        NewsDetailUiState.Loaded(NewsEventsModel())

    fun setItem(item: NewsEventsModel) {
        setState(NewsDetailUiState.Loaded(item))
    }
}