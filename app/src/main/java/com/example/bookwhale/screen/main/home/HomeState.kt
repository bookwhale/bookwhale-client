package com.example.bookwhale.screen.main.home

sealed class HomeState {

    object Uninitialized : HomeState()

    object Loading : HomeState()

    object Success : HomeState()

    data class Error(
        val code: String?
    ) : HomeState()
}
