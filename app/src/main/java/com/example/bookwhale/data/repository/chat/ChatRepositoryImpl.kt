package com.example.bookwhale.data.repository.chat

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.bookwhale.data.network.ChatApiService
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.chat.MakeChatDTO
import com.example.bookwhale.model.MessageType
import com.example.bookwhale.model.main.chat.ChatMessageModel
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.util.DEFAULT_PAGING_CAPACITY
import com.example.bookwhale.util.DEFAULT_PAGING_PARAM
import com.example.bookwhale.widget.adapter.ChatPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatRepositoryImpl(
    private val serverApiService: ServerApiService,
    private val myPreferenceManager: MyPreferenceManager,
    private val chatApiService: ChatApiService,
    private val ioDispatcher: CoroutineDispatcher
) : ChatRepository {
    override suspend fun getChatList(): NetworkResult<List<ChatModel>> = withContext(ioDispatcher) {
        val response = serverApiService.getChatList()

        if (response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.mapIndexed { index, it ->
                    val dateFormat = Regex("[^0-9]")
                    val roomCreateAt = it.roomCreateAt?.replace(dateFormat, "")
                    val lastContentCreateAt = it.lastContentCreateAt?.replace(dateFormat, "")
                    ChatModel(
                        id = index.toLong(),
                        roomId = it.roomId,
                        articleId = it.articleId,
                        articleTitle = it.articleTitle,
                        articleImage = it.articleImage,
                        opponentIdentity = it.opponentIdentity,
                        opponentProfile = it.opponentProfile,
                        roomCreateAt = roomCreateAt,
                        lastContent = it.lastContent,
                        lastContentCreateAt = // 마지막 메세지가 없으면 방 생성 일시를 기준으로 한다.
                        if (it.lastContent.isNullOrEmpty()) roomCreateAt
                        else lastContentCreateAt,
                        opponentDelete = it.opponentDelete
                    )
                }.sortedByDescending { // 채팅방 정렬
                    it.lastContentCreateAt
                }
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun makeNewChat(makeChatDTO: MakeChatDTO): NetworkResult<Boolean> = withContext(ioDispatcher) {
        val response = serverApiService.makeNewChat(makeChatDTO)

        if (response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun getChatRoomDetail(roomId: Int): NetworkResult<List<ChatMessageModel>> = withContext(ioDispatcher) {
        val response = chatApiService.getChatMessages(roomId, DEFAULT_PAGING_PARAM, DEFAULT_PAGING_CAPACITY)

        if (response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.map {
                    if (it.senderId == myPreferenceManager.getId()) {
                        ChatMessageModel(
                            senderId = it.senderId,
                            type = MessageType.MY,
                            senderIdentity = it.senderIdentity,
                            content = it.content,
                            createdDate = it.createdDate
                        )
                    } else {
                        ChatMessageModel(
                            senderId = it.senderId,
                            senderIdentity = it.senderIdentity,
                            content = it.content,
                            createdDate = it.createdDate
                        )
                    }
                }
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun getPreviousMessages(roomId: Int): NetworkResult<Flow<PagingData<ChatMessageModel>>> = withContext(ioDispatcher) {
        val response = Pager(
            PagingConfig(pageSize = 30)
        ) {
            ChatPagingSource(chatApiService, roomId, myPreferenceManager.getId())
        }.flow

        NetworkResult.success(
            response
        )
    }

    override suspend fun deleteChatRoom(roomId: Int): NetworkResult<Boolean> = withContext(ioDispatcher) {
        val response = serverApiService.deleteChatRoom(roomId)

        if (response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }
}
