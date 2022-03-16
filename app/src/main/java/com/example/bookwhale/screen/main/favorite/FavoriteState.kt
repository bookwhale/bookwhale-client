package com.example.bookwhale.screen.main.favorite

import com.example.bookwhale.model.main.favorite.FavoriteModel

sealed class FavoriteState {

    object Uninitialized : FavoriteState()

    object Loading : FavoriteState()

    data class Success(
        val favorites : List<FavoriteModel>
    ) : FavoriteState()

    data class Error(
        val code : String?
    ) : FavoriteState()

}