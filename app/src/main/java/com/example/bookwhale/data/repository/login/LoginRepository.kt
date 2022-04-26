package com.example.bookwhale.data.repository.login

import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.example.bookwhale.model.auth.LoginModel

interface LoginRepository {

    suspend fun getNaverLoginInfo(code: String, deviceToken: String): NetworkResult<LoginModel>

    suspend fun getKaKaoLoginInfo(code: String, deviceToken: String): NetworkResult<LoginModel>

    suspend fun getNewTokens(tokenRequestDTO: TokenRequestDTO): NetworkResult<LoginModel>
}
