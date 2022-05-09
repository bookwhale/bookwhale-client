package com.example.bookwhale.screen.login

import com.example.bookwhale.model.auth.LoginModel

sealed class LoginState {

    object Uninitialized : LoginState()

    object Loading : LoginState()

    object AutoSuccess : LoginState()

    data class Success(
        val apiTokens: LoginModel
    ) : LoginState()

    data class Error(
        val code: String?
    ) : LoginState()
}
