package com.example.bookwhale.data.repository.login

import com.example.bookwhale.model.auth.LoginModel
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.login.TokenRequestDTO

interface LoginRepository {

    suspend fun getNaverLoginInfo(code: String) : LoginModel

    suspend fun getKaKaoLoginInfo(code: String) : LoginModel

    suspend fun getNewTokens(tokenRequestDTO: TokenRequestDTO) : NetworkResult<LoginModel>

}