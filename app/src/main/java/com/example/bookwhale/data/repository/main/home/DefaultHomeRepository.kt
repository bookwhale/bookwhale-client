package com.example.bookwhale.data.repository.main.home

import android.util.Log
import androidx.room.PrimaryKey
import com.example.bookwhale.data.db.AppDataBase
import com.example.bookwhale.data.db.dao.ArticleDao
import com.example.bookwhale.data.entity.home.GetAllArticleEntity
import com.example.bookwhale.data.entity.home.RoomArticleEntity
import com.example.bookwhale.data.network.ServerApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultHomeRepository(
    private val serverApiService: ServerApiService,
    private val articleDao: ArticleDao,
    private val ioDispatcher: CoroutineDispatcher
): HomeRepository {
    override suspend fun getAllArticles(
        search: String?,
        page: Int,
        size: Int,
    ): List<GetAllArticleEntity>? = withContext(ioDispatcher) {
        val response = serverApiService.getAllArticles(search, page, size)

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

    override suspend fun getLocalArticles(): List<RoomArticleEntity>? = withContext(ioDispatcher) {
        articleDao.getArticles()
    }

    override suspend fun insertLocalArticles(articles: GetAllArticleEntity) = withContext(ioDispatcher) {
        articleDao.insertArticles(RoomArticleEntity(
            articleId = articles.articleId,
            articleImage = articles.articleImage,
            articleTitle = articles.articleTitle,
            articlePrice = articles.articlePrice,
            bookStatus = articles.bookStatus,
            sellingLocation = articles.sellingLocation,
            chatCount = articles.chatCount,
            favoriteCount = articles.favoriteCount,
            beforeTime = articles.beforeTime
        ))
    }
}
