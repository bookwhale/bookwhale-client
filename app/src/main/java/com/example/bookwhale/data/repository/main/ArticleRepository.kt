package com.example.bookwhale.data.repository.main

import com.example.bookwhale.data.entity.favorite.FavoriteEntity
import com.example.bookwhale.data.entity.home.ArticleEntity

interface ArticleRepository {

    suspend fun getAllArticles(search: String? = null, page: Int, size: Int) : List<ArticleEntity>?

    suspend fun getLocalArticles() : List<ArticleEntity>?

    suspend fun insertLocalArticles(articles: ArticleEntity)

    suspend fun getFavoriteArticles() : List<FavoriteEntity>?
}