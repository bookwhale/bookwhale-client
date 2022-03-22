package com.example.bookwhale.screen.login

import com.example.bookwhale.model.auth.LoginModel

sealed class LoginState {

    object Uninitialized : LoginState()

    object Loading : LoginState()

    data class Success(
        val apiTokens : LoginModel
    ) : LoginState()

    object Error : LoginState()
}