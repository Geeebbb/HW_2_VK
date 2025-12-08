package com.example.myapplication.data
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CatImage(
    val id: String,
    @SerialName("url") val url: String,
    val width: Int? = null,
    val height: Int? = null
)
