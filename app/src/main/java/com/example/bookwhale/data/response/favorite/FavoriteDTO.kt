package com.example.bookwhale.data.response.favorite

import com.example.bookwhale.model.main.home.ArticleModel

data class FavoriteDTO(
    val favoriteId: Int,
    val articleModel: ArticleModel
)
