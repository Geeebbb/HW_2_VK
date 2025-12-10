package com.example.myapplication.data
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
interface ApiService {
    @GET("v1/images/search")
    suspend fun getCatImages(
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20
    ) : Response<List<CatImage>> //хотим получить ответ в таком формате
}
