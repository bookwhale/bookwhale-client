package com.example.bookwhale.widget.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookwhale.data.network.ChatApiService
import com.example.bookwhale.model.MessageType
import com.example.bookwhale.model.main.chat.ChatMessageModel

class ChatPagingSource(
    private val chatApiService: ChatApiService,
    private val roomId: Int,
    private val userId: Int
) : PagingSource<Int, ChatMessageModel>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, ChatMessageModel> {
        return try {
            val next = params.key ?: 0
            val response = chatApiService.getChatMessages(roomId, next, 30).body()!!.map {
                if (it.senderId == userId) {
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
            LoadResult.Page(
                data = response,
                prevKey = if (next == 0) null else next - 1,
                nextKey = if (response.isEmpty()) null else next + 1 // response에 데이터가 없을 경우 다음페이지를 호출하지 않는다.
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ChatMessageModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
