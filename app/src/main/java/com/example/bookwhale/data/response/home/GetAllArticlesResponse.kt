package com.example.bookwhale.data.response.home

data class GetAllArticlesResponse(
    val articleId: Int,
    val articleImage: String,
    val articleTitle: String,
    val articlePrice: String,
    val bookStatus: String,
    val sellingLocation: String,
    val chatCount: Int,
    val favoriteCount: Int,
    val beforeTime: String
)
