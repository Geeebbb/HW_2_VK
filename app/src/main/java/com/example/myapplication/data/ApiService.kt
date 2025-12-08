package com.example.myapplication.data

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("v1/images/search")
    suspend fun getCatImages(
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 0,
        @Query("mime_types") mime: String? = null
    ): List<CatImage>
}