package com.example.bookwhale.data.network

import com.example.bookwhale.data.response.article.ArticleDTO
import com.example.bookwhale.data.response.article.GetDetailArticleResponse
import com.example.bookwhale.data.response.article.GetNaverBookApiResponse
import com.example.bookwhale.data.response.article.ModifyArticleDTO
import com.example.bookwhale.data.response.chat.GetChatListResponse
import com.example.bookwhale.data.response.chat.GetChatMessageResponse
import com.example.bookwhale.data.response.chat.MakeChatDTO
import com.example.bookwhale.data.response.favorite.AddFavoriteDTO
import com.example.bookwhale.data.response.favorite.AddFavoriteResponse
import com.example.bookwhale.data.response.favorite.GetFavoritesResponse
import com.example.bookwhale.data.response.home.GetAllArticlesResponse
import com.example.bookwhale.data.response.login.LoginResponse
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.example.bookwhale.data.response.my.LogOutDTO
import com.example.bookwhale.data.response.my.MyInfoResponse
import com.example.bookwhale.data.response.my.NickNameRequestDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ServerApiService {

    // 계정
    @GET("api/oauth/naver/login")
    suspend fun getNaverLoginInfo(@Query("code")code: String, @Query("device")device: String) : Response<LoginResponse>

    @GET("api/oauth/kakao/login")
    suspend fun getKaKaoLoginInfo(@Query("code")code: String, @Query("device")device: String) : Response<LoginResponse>

    @POST("api/oauth/refresh")
    suspend fun getNewTokens(@Body tokenRequestDTO: TokenRequestDTO) : Response<LoginResponse>

    // 게시물
    @GET("api/articles")
    suspend fun getAllArticles(@Query("search")search: String?=null, @Query("page")page: Int, @Query("size")size: Int) : Response<List<GetAllArticlesResponse>>

    @GET("api/user/me/favorites")
    suspend fun getFavorites() : Response<List<GetFavoritesResponse>>

    @POST("api/user/me/favorite")
    suspend fun addFavorites(@Body addFavoriteDTO: AddFavoriteDTO) : Response<AddFavoriteResponse>

    @DELETE("api/user/me/favorite/{favoriteId}")
    suspend fun deleteFavorites(@Path("favoriteId")favoriteId : Int) : Response<Unit>

    // 게시물 상세정보
    @GET("api/article/{articleId}")
    suspend fun getDetailArticle(@Path("articleId")articleId : Int) : Response<GetDetailArticleResponse>

    // 게시물 등록
    @GET("api/article/naver-book")
    suspend fun getNaverBookApi(@Query("title")title: String, @Query("display")display: Int, @Query("start")start: Int) : Response<List<GetNaverBookApiResponse>>

    @Multipart
    @POST("api/article")
    suspend fun postArticle(@Part images: List<MultipartBody.Part>, @Part("articleRequest")articleRequest: ArticleDTO): Response<Unit>

    // 게시물 수정
    @Multipart
    @PATCH("api/article/{articleId}")
    suspend fun modifyArticle(@Path("articleId")articleId : Int,@Part images: List<MultipartBody.Part>, @Part("articleUpdateRequest")articleUpdateRequest: ModifyArticleDTO): Response<Unit>

    // 내 게시물
    @GET("api/articles/me")
    suspend fun getMyArticles(): Response<List<GetAllArticlesResponse>>

    // 채팅방
    @GET("api/room")
    suspend fun getChatList() : Response<List<GetChatListResponse>>

    @POST("api/room")
    suspend fun makeNewChat(@Body makeChatDTO: MakeChatDTO) : Response<Unit>

    @GET("api/message/{roomId}")
    suspend fun getChatMessages(@Path("roomId")roomId: Int, @Query("page")page: Int, @Query("size")size: Int) : Response<List<GetChatMessageResponse>>

    // 내 정보
    @GET("api/user/me")
    suspend fun getMyInfo() : Response<MyInfoResponse>

    @PATCH("api/user/me")
    suspend fun updateMyNickName(@Body nickNameRequestDTO: NickNameRequestDTO) : Response<Unit>

    @Multipart
    @PATCH("api/user/profile")
    suspend fun updateProfile(@Part profileImage: MultipartBody.Part): Response<Unit>

    @POST("api/oauth/logout")
    suspend fun logOut(@Body logOutDTO: LogOutDTO): Response<Unit>

    @POST("api/oauth/withdrawal")
    suspend fun withDraw(@Body logOutDTO: LogOutDTO): Response<Unit>


}