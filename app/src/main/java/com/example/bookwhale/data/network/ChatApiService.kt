package com.example.bookwhale.data.network

import com.example.bookwhale.data.response.chat.GetChatMessageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {

    @GET("api/message/{roomId}")
    suspend fun getChatMessages(@Path("roomId")roomId: Int, @Query("page")page: Int, @Query("size")size: Int) : Response<List<GetChatMessageResponse>>

}