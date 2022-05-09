package com.example.bookwhale.screen.splash

/**
 * Splash Api 사용으로 더이상 사용하지 않습니다.
 */

sealed class SplashState {

    object Uninitialized : SplashState()

    object Loading : SplashState()

    object Success : SplashState()

    data class Error(
        val code: String?
    ) : SplashState()
}
