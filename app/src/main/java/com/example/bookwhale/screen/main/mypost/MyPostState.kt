package com.example.bookwhale.screen.main.mypost

import com.example.bookwhale.model.main.favorite.FavoriteModel

sealed class MyPostState {

    object Uninitialized : MyPostState()

    object Loading : MyPostState()

    data class Success(
        val favorites : List<FavoriteModel>
    ) : MyPostState()

    data class Error(
        val code : String?
    ) : MyPostState()

}