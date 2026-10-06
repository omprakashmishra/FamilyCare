package com.omsworld.familycare.ui.news

import androidx.lifecycle.viewModelScope
import com.omsworld.familycare.FamilyCareApp
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.core.Constants
import com.omsworld.familycare.core.result.onError
import com.omsworld.familycare.core.result.onSuccess
import com.omsworld.familycare.data.local.MySharedPreference
import com.omsworld.familycare.data.model.NewsEventsModel
import com.omsworld.familycare.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NewsUiState {
    data object Loading : NewsUiState
    data class Success(val items: List<NewsEventsModel>) : NewsUiState
    data class Error(val message: String) : NewsUiState
}

@HiltViewModel
class NewsEventsViewModel @Inject constructor(
    private val repo: NewsRepository,
    private val prefs: MySharedPreference
) : BaseViewModel<NewsUiState>() {

    override val initialState: NewsUiState = NewsUiState.Loading

    init {
        loadNews()
    }

    fun loadNews() = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        if (userId.isBlank()) {
            setState(NewsUiState.Error("Not logged in"))
            return@launch
        }
        setState(NewsUiState.Loading)
        repo.getNewsList(userId)
            .onSuccess { root ->
                val list = mutableListOf<NewsEventsModel>()
                val arr = root.optJSONArray("news_list")
                if (arr != null) {
                    for (i in 0 until arr.length()) {
                        val o = arr.optJSONObject(i) ?: continue
                        list.add(
                            NewsEventsModel(
                                id = o.optString("id"),
                                title = o.optString("title"),
                                discription = o.optString("discription"),
                                category = o.optString("category"),
                                added_date = o.optString("added_date"),
                                news_type = o.optString("type"),
                                like_count = o.optString("like_count"),
                                like = o.optString("like"),
                                image = o.optString("image")
                            )
                        )
                    }
                }
                setState(NewsUiState.Success(list))
            }
            .onError { msg, _ -> setState(NewsUiState.Error(msg)) }
    }

    fun toggleLike(item: NewsEventsModel) = viewModelScope.launch {
        val ctx = FamilyCareApp.appContext
        val userId = prefs.getString(ctx, Constants.USER_ID, "0")
        val isLiked = item.like == "1"
        val result = if (isLiked) repo.unlikeNews(userId, item.id)
        else repo.likeNews(userId, item.id)
        result.onSuccess { loadNews() }
            .onError { msg, _ -> showError(msg) }
    }
}