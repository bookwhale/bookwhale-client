package com.example.bookwhale.data.repository.main

import com.example.bookwhale.data.db.dao.ArticleDao
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.network.ServerApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultArticleRepository(
    private val serverApiService: ServerApiService,
    private val articleDao: ArticleDao,
    private val ioDispatcher: CoroutineDispatcher
): ArticleRepository {
    override suspend fun getAllArticles(
        search: String?,
        page: Int,
        size: Int,
    ): List<ArticleEntity>? = withContext(ioDispatcher) {
        val response = serverApiService.getAllArticles(search, page, size)

        response.body()?.let{
            it.map { data ->
                ArticleEntity(
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

    override suspend fun getLocalArticles(): List<ArticleEntity>? = withContext(ioDispatcher) {
        articleDao.getArticles()
    }

    override suspend fun insertLocalArticles(articles: ArticleEntity) = withContext(ioDispatcher) {
        articleDao.insertArticles(articles)
    }
}
