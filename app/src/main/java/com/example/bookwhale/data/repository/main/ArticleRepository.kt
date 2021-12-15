package com.example.bookwhale.data.repository.main

import com.example.bookwhale.data.entity.ArticleEntity

interface ArticleRepository {

    suspend fun getArticleList(page: Int, size: Int) : List<ArticleEntity>

}