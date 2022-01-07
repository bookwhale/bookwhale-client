package com.example.bookwhale.data.repository


import com.example.bookwhale.data.entity.favorite.FavoriteEntity
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.repository.main.ArticleRepository

class FakeArticleRepository : ArticleRepository {

    override suspend fun getAllArticles(
        search: String?,
        page: Int,
        size: Int,
    ): List<ArticleEntity>? {
        //
        return null
    }

    override suspend fun getLocalArticles(): List<ArticleEntity>? {
        return listOf(
            ArticleEntity(
                articleId = 0,
                articleImage = "it.articleImage",
                articleTitle = "it.articleTitle",
                articlePrice = "it.articlePrice",
                bookStatus = "it.bookStatus",
                sellingLocation = "it.sellingLocation",
                chatCount = 0,
                favoriteCount = 0,
                beforeTime = "it.beforeTime"
            ),
            ArticleEntity(
                articleId = 1,
                articleImage = "it.articleImage",
                articleTitle = "it.articleTitle",
                articlePrice = "it.articlePrice",
                bookStatus = "it.bookStatus",
                sellingLocation = "it.sellingLocation",
                chatCount = 0,
                favoriteCount = 0,
                beforeTime = "it.beforeTime"
            )
        )
    }

    override suspend fun insertLocalArticles(articles: ArticleEntity) {
        //
    }

    override suspend fun getFavoriteArticles(): List<FavoriteEntity>? {
        return listOf(
            FavoriteEntity(
                favoriteId = 0,
                articleEntity = ArticleEntity(
                    articleId = 0,
                    articleImage = "it.articleImage",
                    articleTitle = "it.articleTitle",
                    articlePrice = "it.articlePrice",
                    bookStatus = "it.bookStatus",
                    sellingLocation = "it.sellingLocation",
                    chatCount = 0,
                    favoriteCount = 0,
                    beforeTime = "it.beforeTime"
                )
            ),
            FavoriteEntity(
                favoriteId = 1,
                articleEntity = ArticleEntity(
                    articleId = 1,
                    articleImage = "it.articleImage",
                    articleTitle = "it.articleTitle",
                    articlePrice = "it.articlePrice",
                    bookStatus = "it.bookStatus",
                    sellingLocation = "it.sellingLocation",
                    chatCount = 0,
                    favoriteCount = 0,
                    beforeTime = "it.beforeTime"
                )
            )
        )
    }


}