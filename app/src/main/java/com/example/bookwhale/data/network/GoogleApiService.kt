package com.example.bookwhale.data.network

import com.example.bookwhale.data.response.login.LoginGoogleRequestDTO
import com.example.bookwhale.data.response.login.LoginGoogleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface GoogleApiService {

    @POST("token")
    suspend fun fetchGoogleAuthInfo(@Body request: LoginGoogleRequestDTO): Response<LoginGoogleResponse>
    /*구글 서버에 IdToken을 보냄*/

}