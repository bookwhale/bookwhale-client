package com.example.bookwhale.data.repository.main.home

import com.example.bookwhale.data.entity.ArticleEntity
import com.example.bookwhale.data.entity.home.GetAllArticleEntity
import com.example.bookwhale.data.entity.home.RoomArticleEntity
import com.example.bookwhale.data.response.home.GetAllArticlesResponse

interface HomeRepository {

    suspend fun getAllArticles(search: String? = null, page: Int, size: Int) : List<GetAllArticleEntity>?

    suspend fun getLocalArticles() : List<RoomArticleEntity>?

    suspend fun insertLocalArticles(articles: GetAllArticleEntity)
}