package com.example.bookwhale.screen.main.my

import com.example.bookwhale.model.auth.ProfileModel

sealed class MyState {

    object Uninitialized : MyState()

    object Loading : MyState()

    object LogOutSuccess : MyState()

    object WithDrawSuccess : MyState()

    data class Success(
        val myInfo : ProfileModel
    ) : MyState()

    data class Error(
        val code : String?
    ) : MyState()

}