package com.example.bookwhale.screen.splash

sealed class SplashState {

    object Uninitialized : SplashState()

    object Loading : SplashState()

    object Success : SplashState()

    data class Error(
        val code: String?
    ) : SplashState()
}
