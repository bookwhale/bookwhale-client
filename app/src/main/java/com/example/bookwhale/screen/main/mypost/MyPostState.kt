package com.example.bookwhale.screen.main.mypost

import com.example.bookwhale.model.main.home.ArticleModel

sealed class MyPostState {

    object Uninitialized : MyPostState()

    object Loading : MyPostState()

    data class Success(
        val myArticles: List<ArticleModel>
    ) : MyPostState()

    data class Error(
        val code: String?
    ) : MyPostState()
}
