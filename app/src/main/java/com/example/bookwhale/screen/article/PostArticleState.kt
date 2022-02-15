package com.example.bookwhale.screen.article

import com.example.bookwhale.model.article.DetailArticleModel
import com.example.bookwhale.model.main.favorite.FavoriteModel

sealed class PostArticleState {

    object Uninitialized : PostArticleState()

    object Loading : PostArticleState()

    object Success : PostArticleState()

    data class Error(
        val code : String?
    ) : PostArticleState()

}