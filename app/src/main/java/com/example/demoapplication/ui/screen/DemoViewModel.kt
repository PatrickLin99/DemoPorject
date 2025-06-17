package com.example.demoapplication.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapplication.data.ApiStatus
import com.example.demoapplication.data.ItemsRepository
import com.example.demoapplication.model.UiState
import kotlinx.coroutines.launch

class DemoViewModel(private val itemsRepository: ItemsRepository): ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Loading)
    var screenShot by mutableStateOf<MutableMap<String, Bitmap>>(mutableMapOf())

    init {
        getItems()
    }

    private fun getItems() {
        viewModelScope.launch {
            uiState = UiState.Loading
            Log.e("TestTest","getItems 00")
            uiState = when(val apiStatus = itemsRepository.getItemsUrl()) {
                is ApiStatus.Success -> {
                    Log.e("TestTest","Success")
                    UiState.Success(apiStatus.data)
                }
                is ApiStatus.Error -> {
                    Log.e("TestTest","Error 1")
                    UiState.Error
                }
                is ApiStatus.Exception -> {
                    Log.e("TestTest","Error 2")
                    UiState.Error
                }
            }
        }
    }

}