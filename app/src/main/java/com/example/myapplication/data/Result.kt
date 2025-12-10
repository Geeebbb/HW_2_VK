package com.example.myapplication.data

sealed class Result<out T> {
    abstract val data: T?
    data class Ok<out T>(val value: T) : Result<T>() {
        override val data: T = value
    }
    data class Error(val message: String) : Result<Nothing>() {
        override val data: Nothing? = null
    }
}
