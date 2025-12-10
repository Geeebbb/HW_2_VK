package com.example.myapplication.data
class DataRepository(private val api: ApiService) {

    private val memoryCache = mutableListOf<CatImage>() //загруженных картинок
    private val loadedPages = mutableSetOf<Int>() //множество заггруженных страниц
    var isLoading = false
        private set

    suspend fun loadPage(page: Int, pageSize: Int = 20): Result<List<CatImage>> {
        if (isLoading) return Result.Error("Загрузка уже идет")
        if (loadedPages.contains(page)) return Result.Ok(memoryCache.toList()) //если страницу уже загружали
        return try {
            isLoading = true
            val response = api.getCatImages(page = page, limit = pageSize)//делаем сетевой вызов
            if (response.isSuccessful) {
                val body = response.body()//получаем список картинок
                if (!body.isNullOrEmpty()) {
                    memoryCache += body
                    loadedPages += page
                    Result.Ok(memoryCache.toList())
                } else {
                    Result.Error("Пустой ответ от сервера")
                }
            } else {
                Result.Error("HTTP ошибка: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error("Ошибка сети: ${e.localizedMessage ?: "неизвестная ошибка"}")
        } finally {
            isLoading = false
        }
    }

    fun cachedItems(): List<CatImage> = memoryCache.toList()
}
