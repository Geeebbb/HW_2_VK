package com.example.myapplication.uii

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.CatImage
import com.example.myapplication.data.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed interface UiState {
    object Loading : UiState
    data class Success(val images: List<CatImage>) : UiState
    data class Error(val message: String) : UiState
}

class MainViewModel(private val repo: DataRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)

    val state: StateFlow<UiState> = _state

    private var page = 0


    init {
        loadNextPage()
    }

    fun loadNextPage() {
        viewModelScope.launch {
            try {
                _state.value = UiState.Loading

                val data = repo.loadPage(page++)
                _state.value = UiState.Success(data as List<CatImage>)
            } catch (e: Exception) {
                _state.value = UiState.Error("Ошибка загрузки :(")
            }
        }
    }
}