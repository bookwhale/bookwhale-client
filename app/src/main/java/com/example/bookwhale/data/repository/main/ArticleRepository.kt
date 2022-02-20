package com.example.bookwhale.data.repository.main

import androidx.paging.PagingData
import com.example.bookwhale.data.entity.favorite.FavoriteEntity
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.favorite.AddFavoriteDTO
import com.example.bookwhale.model.main.home.ArticleModel
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {

    suspend fun getAllArticles(search: String? = null, page: Int, size: Int) : NetworkResult<List<ArticleEntity>>

    suspend fun getLocalArticles() : NetworkResult<List<ArticleEntity>>

    suspend fun insertLocalArticles(articles: ArticleEntity)

    suspend fun getFavoriteArticles() : NetworkResult<List<FavoriteEntity>>

    suspend fun addFavoriteArticle(addFavoriteDTO: AddFavoriteDTO) : NetworkResult<Int>

    suspend fun deleteFavoriteArticle(favoriteId: Int) : NetworkResult<Boolean>

    suspend fun getAllArticlesPaging(search: String? = null) : NetworkResult<Flow<PagingData<ArticleModel>>>

    suspend fun getMyArticles() : NetworkResult<List<ArticleModel>>
}