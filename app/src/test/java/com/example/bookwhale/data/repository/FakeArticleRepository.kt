package com.example.bookwhale.data.repository

import com.example.bookwhale.data.entity.favorite.FavoriteEntity
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.response.NetworkResult

class FakeArticleRepository : ArticleRepository {

    override suspend fun getAllArticles(
        search: String?,
        page: Int,
        size: Int,
    ): NetworkResult<List<ArticleEntity>> {
        //
        return NetworkResult.error(code="")
    }

    override suspend fun getLocalArticles(): NetworkResult<List<ArticleEntity>> {

        val data = listOf(
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

        return NetworkResult.success(
            data = data
        )
    }

    override suspend fun insertLocalArticles(articles: ArticleEntity) {
        //
    }

    override suspend fun getFavoriteArticles(): NetworkResult<List<FavoriteEntity>> {

        val data = listOf(
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

        return NetworkResult.success(
            data = data
        )
    }


}