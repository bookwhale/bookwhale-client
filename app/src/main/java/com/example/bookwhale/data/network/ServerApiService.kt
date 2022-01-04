package com.example.bookwhale.data.network

import com.example.bookwhale.data.response.home.GetAllArticlesResponse
import com.example.bookwhale.data.response.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ServerApiService {

    @GET("api/oauth/naver")
    suspend fun testNaver() : Response<Unit>

    @GET("api/oauth/google")
    suspend fun testGoogle() : Response<Unit>

    @GET("api/oauth/naver/login")
    suspend fun getNaverLoginInfo(@Query("code")code: String) : Response<LoginResponse>

    @GET("api/oauth/google/login")
    suspend fun getGoogleLoginInfo(@Query("code")code: String) : Response<LoginResponse>

//    GET /api/articles?search=%EC%B1%85%20%EC%A0%9C%EB%AA%A9&page=0&size=10

    @GET("api/articles")
    suspend fun getAllArticles(@Query("search")search: String?=null, @Query("page")page: Int, @Query("size")size: Int) : Response<List<GetAllArticlesResponse>>

}