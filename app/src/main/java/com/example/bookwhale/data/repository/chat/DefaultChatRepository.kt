package com.example.bookwhale.data.repository.chat

import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.model.main.home.ArticleModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultChatRepository(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher
) : ChatRepository {
    override suspend fun getChatList(): NetworkResult<List<ChatModel>> = withContext(ioDispatcher) {
        val response = serverApiService.getChatList()

        if(response.isSuccessful) {
            NetworkResult.success(
                response.body()!!.map {
                    ChatModel(
                        id = it.hashCode().toLong(),
                        roomId = it.roomId,
                        articleId = it.articleId,
                        articleImage = it.articleImage,
                        opponentIdentity = it.opponentIdentity,
                        opponentProfile = it.opponentProfile,
                        opponentDelete = it.opponentDelete
                    )
                }
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }
}
