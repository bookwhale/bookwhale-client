package com.example.bookwhale.data.repository.main

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.favorite.AddFavoriteDTO
import com.example.bookwhale.data.response.favorite.FavoriteDTO
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.widget.adapter.ArticlePagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ArticleRepositoryImpl(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher
) : ArticleRepository {
    override suspend fun getFavoriteArticles(): NetworkResult<List<FavoriteDTO>> = withContext(ioDispatcher) {

        val response = serverApiService.getFavorites()

        if (response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.let {
                    it.mapIndexed { index, data ->
                        FavoriteDTO(
                            favoriteId = data.favoriteId,
                            articleModel = ArticleModel(
                                id = index.toLong(),
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
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun addFavoriteArticle(addFavoriteDTO: AddFavoriteDTO): NetworkResult<Int> = withContext(ioDispatcher) {

        val response = serverApiService.addFavorites(addFavoriteDTO)

        if (response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.favoriteId
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun deleteFavoriteArticle(favoriteId: Int): NetworkResult<Boolean> = withContext(ioDispatcher) {
        val response = serverApiService.deleteFavorites(favoriteId)

        if (response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun getAllArticlesPaging(
        search: String?
    ): NetworkResult<Flow<PagingData<ArticleModel>>> = withContext(ioDispatcher) {

        val response = Pager(
            PagingConfig(pageSize = 10)
        ) {
            ArticlePagingSource(serverApiService, search)
        }.flow

        NetworkResult.success(
            response
        )
    }

    override suspend fun getMyArticles(): NetworkResult<List<ArticleModel>> = withContext(ioDispatcher) {
        val response = serverApiService.getMyArticles()

        if (response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.mapIndexed { index, data ->
                    ArticleModel(
                        id = index.toLong(),
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
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }
}
