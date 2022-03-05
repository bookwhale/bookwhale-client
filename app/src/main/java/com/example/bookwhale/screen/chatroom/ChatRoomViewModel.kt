package com.example.bookwhale.screen.chatroom

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ChatRoomViewModel(
    private val chatRepository: ChatRepository
): BaseViewModel() {
//     const val CHAT_URL = "http://52.79.148.89:8081"
    val url = "ws://52.79.148.89:8081/ws/websocket" // 소켓에 연결하는 엔드포인트가 /socket일때 다음과 같음
    val stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

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

        Log.e("my",myPreferenceManager.getId().toString())

        return response.data!!.cachedIn(viewModelScope)
    }


    fun sendMessage(roomId: Int, message: String) {
        val data = JSONObject()
        data.put("roomId", roomId.toString())
        data.put("senderId", myPreferenceManager.getId().toString())
        data.put("senderIdentity", myPreferenceManager.getName())
        data.put("content", message)

        stompClient.send("/pub/chat/message", data.toString()).subscribe()
    }
}
