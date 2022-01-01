package com.example.bookwhale.data.network

import com.example.bookwhale.data.response.login.NaverLoginResponse
import com.nhn.android.naverlogin.data.OAuthLoginState
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApiService {

    @GET("api/oauth/naver")
    suspend fun testNaver() : Response<Unit>

    @GET("api/oauth/google")
    suspend fun testGoogle() : Response<Unit>

    @GET("api/oauth/naver/login")
    suspend fun getNaverLoginInfo(@Query("code")code: String) : Response<NaverLoginResponse>

}