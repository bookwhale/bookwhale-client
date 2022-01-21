package com.example.bookwhale.screen.splash

import com.example.bookwhale.model.main.my.ProfileModel

sealed class SplashState {

    object Uninitialized : SplashState()

    object Loading : SplashState()

    object Success : SplashState()

    data class Error(
        val code : String?
    ) : SplashState()

}