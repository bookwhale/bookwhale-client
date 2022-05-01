package com.example.bookwhale.data.repository.chat

import androidx.paging.PagingData
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.chat.MakeChatDTO
import com.example.bookwhale.model.main.chat.ChatMessageModel
import com.example.bookwhale.model.main.chat.ChatModel
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun getChatList(): NetworkResult<List<ChatModel>>

    suspend fun makeNewChat(makeChatDTO: MakeChatDTO): NetworkResult<Boolean>

    suspend fun getChatRoomDetail(roomId: Int): NetworkResult<List<ChatMessageModel>>

    suspend fun getPreviousMessages(roomId: Int): NetworkResult<Flow<PagingData<ChatMessageModel>>>

    suspend fun checkRoomStates(roomId: Int): NetworkResult<ChatModel>

    suspend fun deleteChatRoom(roomId: Int): NetworkResult<Boolean>
}
