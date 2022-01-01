package com.example.bookwhale.data.repository.login

import com.example.bookwhale.data.entity.login.NaverLoginEntity
import com.nhn.android.naverlogin.data.OAuthLoginState

interface LoginRepository {

    suspend fun test()

    suspend fun test2()

    suspend fun getNaverLoginInfo(code: String) : NaverLoginEntity

}