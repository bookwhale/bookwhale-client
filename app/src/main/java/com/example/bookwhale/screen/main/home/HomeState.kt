package com.example.bookwhale.screen.main.home

import com.example.bookwhale.data.entity.login.LoginEntity

sealed class HomeState {

    object Uninitialized : HomeState()

    object Loading : HomeState()

    data class Success(
        val apiTokens : LoginEntity
    ) : HomeState()

    object Error : HomeState()
}