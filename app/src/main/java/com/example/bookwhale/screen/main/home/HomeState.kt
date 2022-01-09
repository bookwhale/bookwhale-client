package com.example.bookwhale.screen.main.home

import com.example.bookwhale.data.entity.login.LoginEntity
import com.example.bookwhale.model.main.home.ArticleModel

sealed class HomeState {

    object Uninitialized : HomeState()

    object Loading : HomeState()

    data class Success(
        val articles : List<ArticleModel>
    ) : HomeState()

    object Error : HomeState()
}