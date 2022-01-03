package com.example.bookwhale.screen.login

import com.example.bookwhale.data.entity.login.LoginEntity

sealed class LoginState {

    object Uninitialized : LoginState()

    object Loading : LoginState()

    data class Success(
        val apiTokens : LoginEntity
    ) : LoginState()

    object Error : LoginState()
}