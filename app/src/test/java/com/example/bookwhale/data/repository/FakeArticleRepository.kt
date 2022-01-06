package com.example.bookwhale.data.repository


import com.example.bookwhale.data.entity.home.GetAllArticleEntity
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.repository.main.ArticleRepository

class FakeArticleRepository : ArticleRepository {

    override suspend fun getAllArticles(
        search: String?,
        page: Int,
        size: Int,
    ): List<GetAllArticleEntity>? {
        //
        return null
    }

    override suspend fun getLocalArticles(): List<ArticleEntity>? {
        //
        return null
    }

    override suspend fun insertLocalArticles(articles: GetAllArticleEntity) {
        //
    }


}