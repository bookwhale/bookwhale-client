package com.example.bookwhale.data.repository.login

import com.example.bookwhale.data.entity.login.LoginEntity
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.login.LoginGoogleResponse
import com.example.bookwhale.data.response.login.TokenRequestDTO

interface LoginRepository {

    suspend fun getNaverLoginInfo(code: String) : LoginEntity

    suspend fun getKaKaoLoginInfo(code: String) : LoginEntity

    suspend fun getNewTokens(tokenRequestDTO: TokenRequestDTO) : NetworkResult<LoginEntity>

}