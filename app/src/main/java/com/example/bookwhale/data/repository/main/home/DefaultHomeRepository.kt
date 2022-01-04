package com.example.bookwhale.data.repository.main.home

import android.util.Log
import com.example.bookwhale.data.entity.home.GetAllArticleEntity
import com.example.bookwhale.data.network.ServerApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultHomeRepository(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher
): HomeRepository {
    override suspend fun getAllArticles(
        search: String?,
        page: Int,
        size: Int,
    ): List<GetAllArticleEntity>? = withContext(ioDispatcher) {
        var response = serverApiService.getAllArticles(search, page, size)

        response.body()?.let{
            it.map { data ->
                GetAllArticleEntity(
                    articleId = data.articleId,
                    articleImage = data.articleImage,
                    articleTitle = data.articleTitle,
                    articlePrice = data.articlePrice,
                    bookStatus = data.bookStatus,
                    sellingLocation = data.sellingLocation,
                    chatCount = data.chatCount,
                    favoriteCount = data.favoriteCount,
                    beforeTime = data.beforeTime
                )
            }
        }?: kotlin.run {
            null
        }

    }
}
