package com.example.bookwhale.screen.chatroom

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.model.main.chat.ChatMessageModel
import com.example.bookwhale.model.main.chat.MessageType
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ChatRoomViewModel(
    private val chatRepository: ChatRepository
): BaseViewModel() {

    var tempData = arrayListOf<ChatMessageModel>()

    fun load(roomId: Int) = viewModelScope.launch {
        chatRepository.getChatRoomDetail(roomId)

        val myId = myPreferenceManager.getId()

        val temp = listOf<ChatMessageModel>(
            ChatMessageModel(
                senderId = myId,
                type = MessageType.MY,
                senderIdentity = "내 닉네임",
                content = "안녕",
                createdDate = "시간"
            ), ChatMessageModel(
                senderId = 45345,
                type = MessageType.OPPONENT,
                senderIdentity = "상대 닉네임",
                content = "안녕?",
                createdDate = "시간"
            )
        )

        tempData.addAll(temp)
    }


    suspend fun getPreviousMessages(roomId: Int) : Flow<PagingData<ChatMessageModel>> {

        val response = chatRepository.getPreviousMessages(roomId)

        return response.data!!.cachedIn(viewModelScope)
    }
}
