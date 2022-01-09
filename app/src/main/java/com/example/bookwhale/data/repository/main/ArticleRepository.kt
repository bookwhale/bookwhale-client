package com.example.bookwhale.data.repository.main

import com.example.bookwhale.data.entity.favorite.FavoriteEntity
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.response.NetworkResult

interface ArticleRepository {

    suspend fun getAllArticles(search: String? = null, page: Int, size: Int) : NetworkResult<List<ArticleEntity>>

    suspend fun getLocalArticles() : NetworkResult<List<ArticleEntity>>

    suspend fun insertLocalArticles(articles: ArticleEntity)

    suspend fun getFavoriteArticles() : NetworkResult<List<FavoriteEntity>>
}