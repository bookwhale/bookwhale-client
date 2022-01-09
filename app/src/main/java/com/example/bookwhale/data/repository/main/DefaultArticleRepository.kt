package com.example.bookwhale.data.repository.main

import com.example.bookwhale.data.db.dao.ArticleDao
import com.example.bookwhale.data.entity.favorite.FavoriteEntity
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorResponse
import com.example.bookwhale.data.response.NetworkResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    ): NetworkResult<List<ArticleEntity>> = withContext(ioDispatcher) {
        val response = serverApiService.getAllArticles(search, page, size)

        if(response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.map {
                    ArticleEntity(
                        articleId = it.articleId,
                        articleImage = it.articleImage,
                        articleTitle = it.articleTitle,
                        articlePrice = it.articlePrice,
                        bookStatus = it.bookStatus,
                        sellingLocation = it.sellingLocation,
                        chatCount = it.chatCount,
                        favoriteCount = it.favoriteCount,
                        beforeTime = it.beforeTime
                    )
                }
            )
        } else {
            val responseErrorString = response.errorBody()?.string()
            val type = object : TypeToken<ErrorResponse?>() {}.type
            val responseError: ErrorResponse = Gson().fromJson(responseErrorString, type)

            NetworkResult.error(code = responseError.code!!)
        }
    }

    override suspend fun getLocalArticles(): NetworkResult<List<ArticleEntity>> = withContext(ioDispatcher) {
        val response = articleDao.getArticles()
        NetworkResult.success( response )
    }

    override suspend fun insertLocalArticles(articles: ArticleEntity) = withContext(ioDispatcher) {
        articleDao.insertArticles(articles)
    }

    override suspend fun getFavoriteArticles(): NetworkResult<List<FavoriteEntity>> = withContext(ioDispatcher) {

        val response = serverApiService.getFavorites()

        if(response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.let {
                    it.map { data ->
                        FavoriteEntity(
                            favoriteId = data.favoriteId,
                            articleEntity = ArticleEntity(
                                articleId = data.articlesResponse.articleId,
                                articleImage = data.articlesResponse.articleImage,
                                articleTitle = data.articlesResponse.articleTitle,
                                articlePrice = data.articlesResponse.articlePrice,
                                bookStatus = data.articlesResponse.bookStatus,
                                sellingLocation = data.articlesResponse.sellingLocation,
                                chatCount = data.articlesResponse.chatCount,
                                favoriteCount = data.articlesResponse.favoriteCount,
                                beforeTime = data.articlesResponse.beforeTime
                            )
                        )
                    }
                }
            )
        } else {
            val responseErrorString = response.errorBody()?.string()
            val type = object : TypeToken<ErrorResponse?>() {}.type
            val responseError: ErrorResponse = Gson().fromJson(responseErrorString, type)

            NetworkResult.error(code = responseError.code!!)
        }

    }
}
