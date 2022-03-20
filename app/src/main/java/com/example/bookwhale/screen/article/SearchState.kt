package com.example.bookwhale.screen.article

sealed class SearchState {

    object Uninitialized : SearchState()

    object Loading : SearchState()

    object Success : SearchState()

    data class Error(
        val code : String?
    ) : SearchState()

}