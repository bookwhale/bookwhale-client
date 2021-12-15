package com.example.bookwhale.data.repository.main

import com.example.bookwhale.data.entity.ArticleEntity
import com.example.bookwhale.data.entity.LikeArticleEntity

interface ArticleRepository {

    suspend fun getArticleList(page: Int, size: Int) : List<ArticleEntity>

    suspend fun getLikeArticleList() : List<LikeArticleEntity>
}