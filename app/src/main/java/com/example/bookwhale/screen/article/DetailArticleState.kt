package com.example.bookwhale.screen.article

import com.example.bookwhale.model.article.DetailArticleModel
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.my.ProfileModel

sealed class DetailArticleState {

    object Uninitialized : DetailArticleState()

    object Loading : DetailArticleState()

    data class FavoriteSuccess(
        val favoriteList : List<FavoriteModel>
    ) : DetailArticleState()

    data class Success(
        val article : DetailArticleModel
    ) : DetailArticleState()

    data class Error(
        val code : String?
    ) : DetailArticleState()

}