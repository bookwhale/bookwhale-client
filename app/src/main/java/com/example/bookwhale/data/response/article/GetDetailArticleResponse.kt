package com.example.bookwhale.data.response.article

data class GetDetailArticleResponse(
    val sellerId: Int,
    val sellerIdentity: String,
    val sellerProfileImage: String,
    val articleId: Int,
    val title: String,
    val price: String,
    val description: String,
    val bookStatus: String,
    val articleStatus: String,
    val sellingLocation: String,
    val images: List<String>,
    val bookResponse: BookResponse,
    val viewCount: Int,
    val myFavoriteId: Int?,
    val favoriteCount: Int,
    val beforeTime: String,
    val myArticle: Boolean,
    val myFavorite: Boolean
)
