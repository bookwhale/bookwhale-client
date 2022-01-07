package com.example.bookwhale.data.entity.favorite

import androidx.room.PrimaryKey
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.response.home.GetAllArticlesResponse

data class FavoriteEntity (
    val favoriteId: Int,
    val articleEntity: ArticleEntity
)