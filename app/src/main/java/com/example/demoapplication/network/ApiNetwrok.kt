package com.example.demoapplication.network

import com.example.demoapplication.model.ItemsUrl
import retrofit2.Response
import retrofit2.http.GET

interface ApiNetwork {
    @GET("pages")
    suspend fun getUrls(): Response<ItemsUrl>
}