package com.example.bookwhale.data.network

import com.example.bookwhale.data.response.favorite.GetFavoritesResponse
import com.example.bookwhale.data.response.home.GetAllArticlesResponse
import com.example.bookwhale.data.response.login.LoginResponse
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.example.bookwhale.data.response.my.MyInfoResponse
import com.example.bookwhale.data.response.my.NickNameRequestDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ServerApiService {

    // 계정
    @GET("api/oauth/naver/login")
    suspend fun getNaverLoginInfo(@Query("code")code: String) : Response<LoginResponse>

    @GET("api/oauth/kakao/login")
    suspend fun getKaKaoLoginInfo(@Query("code")code: String) : Response<LoginResponse>

    @POST("api/oauth/refresh")
    suspend fun getNewTokens(@Body tokenRequestDTO: TokenRequestDTO) : Response<LoginResponse>

    // 계시물
    @GET("api/articles")
    suspend fun getAllArticles(@Query("search")search: String?=null, @Query("page")page: Int, @Query("size")size: Int) : Response<List<GetAllArticlesResponse>>

    @GET("api/user/me/favorites")
    suspend fun getFavorites() : Response<List<GetFavoritesResponse>>

    // 내 정보
    @GET("api/user/me")
    suspend fun getMyInfo() : Response<MyInfoResponse>

    @PATCH("api/user/me")
    suspend fun updateMyNickName(@Body nickNameRequestDTO: NickNameRequestDTO) : Response<Unit>

    @Multipart
    @PATCH("api/user/profile")
    suspend fun updateProfile(@Part profileImage: MultipartBody.Part): Response<Unit>
}