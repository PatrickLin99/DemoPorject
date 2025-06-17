package com.example.demoapplication.data

import android.util.Log
import com.example.demoapplication.model.ItemsUrl
import com.example.demoapplication.network.ApiNetwork

interface ItemsRepository {
    suspend fun getItemsUrl(): ApiStatus<ItemsUrl>
}

class ItemsRepositoryImpl(private val apiNetwork: ApiNetwork): ItemsRepository {
    override suspend fun getItemsUrl(): ApiStatus<ItemsUrl> {
        return try {
            val apiResponse = apiNetwork.getUrls()
            if (apiResponse.isSuccessful) {
                apiResponse.body()?.let {
                    ApiStatus.Success(it)
                } ?: ApiStatus.Error("Response is Successful but empty body")
            } else {
                ApiStatus.Error("Response is NOT Successful")
            }

        } catch (e: Exception) {
            Log.e("TestTest","ItemsRepositoryImpl $e")
            ApiStatus.Exception(e)
        }
    }
}