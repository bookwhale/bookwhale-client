package com.example.bookwhale.screen.article

sealed class PostArticleState {

    object Uninitialized : PostArticleState()

    object Loading : PostArticleState()

    object Success : PostArticleState()

    data class Error(
        val code : String?
    ) : PostArticleState()

}