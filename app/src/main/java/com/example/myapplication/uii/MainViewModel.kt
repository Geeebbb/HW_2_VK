package com.example.myapplication.uii

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.CatImage
import com.example.myapplication.data.Result
import com.example.myapplication.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    object LoadingInitial : UiState
    data class Success(val images: List<CatImage>, val isLoadingMore: Boolean = false) : UiState
    data class Error(val message: String, val isInitial: Boolean = true) : UiState
    companion object {
        val Loading: UiState = LoadingInitial
    }
}


class MainViewModel(
    private val repo: DataRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.LoadingInitial)
    val state: StateFlow<UiState> = _state

    private var page = savedStateHandle.get<Int>("page") ?: 0
    private val pageSize = 20

    init {
        val cached = repo.cachedItems()
        if (cached.isNotEmpty()) {
            _state.value = UiState.Success(cached, isLoadingMore = false)
        } else {
            loadNextPage(initial = true)
        }
    }

    fun loadNextPage(initial: Boolean = false) {
        if (repo.isLoading) return
        viewModelScope.launch {
            if (initial) _state.value = UiState.LoadingInitial
            else {
                val cur = (_state.value as? UiState.Success)
                if (cur != null) _state.value = cur.copy(isLoadingMore = true)
            }

            when (val result = repo.loadPage(page, pageSize)) {
                is Result.Ok -> {
                    page++
                    savedStateHandle["page"] = page
                    _state.value = UiState.Success(result.data ?: emptyList(), isLoadingMore = false)
                }
                is Result.Error -> {
                    val isInitialError = initial && (repo.cachedItems().isEmpty())
                    _state.value = UiState.Error(result.message, isInitial = isInitialError)
                }
            }
        }
    }

    fun retry() {
        val current = _state.value
        if (current is UiState.Error) {
            loadNextPage(initial = current.isInitial)
        }
    }
}