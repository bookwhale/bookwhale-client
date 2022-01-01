package com.example.bookwhale.screen.login

import com.example.bookwhale.data.entity.login.NaverLoginEntity

sealed class LoginState {

    object Uninitialized : LoginState()

    object Loading : LoginState()

    data class Success(
        val apiTokens : NaverLoginEntity
    ) : LoginState()

    object Error : LoginState()
}