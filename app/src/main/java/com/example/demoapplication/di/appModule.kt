package com.example.demoapplication.di

import com.example.demoapplication.data.ItemsRepository
import com.example.demoapplication.data.ItemsRepositoryImpl
import com.example.demoapplication.network.ApiNetwork
import com.example.demoapplication.ui.screen.DemoViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val appModule = module {

    single {
        OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit
            .Builder()
            .client(get())
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .baseUrl("https://private-58ab56-mocks3.apiary-mock.com/")
            .build()
    }

    single<ApiNetwork> {
        get<Retrofit>().create(ApiNetwork::class.java)
    }

    single<ItemsRepository> {
        ItemsRepositoryImpl(get())
    }

    viewModel {
        DemoViewModel(get())
    }

}