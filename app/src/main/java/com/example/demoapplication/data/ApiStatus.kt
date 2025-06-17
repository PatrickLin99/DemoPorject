package com.example.demoapplication.data

sealed class ApiStatus<out T> {
    data class Success<T>(val data: T): ApiStatus<T>()
    data class Error(val message: String): ApiStatus<Nothing>()
    data class Exception(val e: kotlin.Exception): ApiStatus<Nothing>()
}