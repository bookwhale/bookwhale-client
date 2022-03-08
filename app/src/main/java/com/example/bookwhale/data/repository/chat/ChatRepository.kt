package com.example.bookwhale.data.repository.chat

import androidx.paging.PagingData
import com.example.bookwhale.data.entity.login.LoginEntity
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.chat.MakeChatDTO
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.example.bookwhale.model.main.chat.ChatMessageModel
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.model.main.home.ArticleModel
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun getChatList() : NetworkResult<List<ChatModel>>

    suspend fun makeNewChat(makeChatDTO: MakeChatDTO) : NetworkResult<Boolean>

    suspend fun getChatRoomDetail(roomId: Int) : NetworkResult<List<ChatMessageModel>>

    suspend fun getPreviousMessages(roomId: Int) : NetworkResult<Flow<PagingData<ChatMessageModel>>>

}