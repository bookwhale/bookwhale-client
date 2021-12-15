package com.example.bookwhale.data.repository

import com.example.bookwhale.data.entity.ArticleEntity
import com.example.bookwhale.data.entity.LikeArticleEntity
import com.example.bookwhale.data.repository.main.ArticleRepository

class FakeArticleRepository : ArticleRepository {


    override suspend fun getArticleList(page: Int, size: Int): List<ArticleEntity> {

        return listOf(
            ArticleEntity(
                postId = 1,
                postImage = "",
                postTitle = "상태 좋은 책 팝니다",
                postPrice = "1000원",
                postStatus = "판매중",
                bookTitle = "책 제목",
                bookAuthor = "저자",
                bookPublisher = "출판사",
                sellingLocation = "서울",
                viewCount = 1,
                likeCount = 1,
                beforeTime = "10분 전"
            )
        )
    }

    override suspend fun getLikeArticleList(): List<LikeArticleEntity> {

        return listOf(
            LikeArticleEntity(
                likeId = 0,
                postResponse = ArticleEntity(
                    postId = 1,
                    postImage = "",
                    postTitle = "상태 좋은 책 팝니다",
                    postPrice = "1000원",
                    postStatus = "판매중",
                    bookTitle = "책 제목",
                    bookAuthor = "저자",
                    bookPublisher = "출판사",
                    sellingLocation = "서울",
                    viewCount = 1,
                    likeCount = 1,
                    beforeTime = "10분 전"
                )
            )
        )

    }


}