package com.example.myapplication.data
class DataRepository(private val api: ApiService)
{ private val memoryCache = mutableListOf<CatImage>()
    suspend fun loadPage(page: Int): List<CatImage>
    { val newImages = api.getCatImages(limit = 20, page = page)
        memoryCache += newImages
        return memoryCache.toList() } }