package com.example.bookwhale.data.network

import com.example.bookwhale.data.response.favorite.GetFavoritesResponse
import com.example.bookwhale.data.response.home.GetAllArticlesResponse
import com.example.bookwhale.data.response.login.LoginResponse
import com.example.bookwhale.data.response.login.TokenRequestDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ServerApiService {

    @GET("api/oauth/naver/login")
    suspend fun getNaverLoginInfo(@Query("code")code: String) : Response<LoginResponse>

    @GET("api/oauth/kakao/login")
    suspend fun getKaKaoLoginInfo(@Query("code")code: String) : Response<LoginResponse>

    @POST("api/oauth/refresh")
    suspend fun getNewTokens(@Body tokenRequestDTO: TokenRequestDTO) : Response<LoginResponse>

    @GET("api/articles")
    suspend fun getAllArticles(@Query("search")search: String?=null, @Query("page")page: Int, @Query("size")size: Int) : Response<List<GetAllArticlesResponse>>

    @GET("api/user/me/favorites")
    suspend fun getFavorites() : Response<List<GetFavoritesResponse>>
}