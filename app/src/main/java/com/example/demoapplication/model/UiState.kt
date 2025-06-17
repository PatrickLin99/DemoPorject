package com.example.demoapplication.model

sealed class UiState {
    data class Success(val item: ItemsUrl):UiState()
    data object Error: UiState()
    data object Loading: UiState()
}