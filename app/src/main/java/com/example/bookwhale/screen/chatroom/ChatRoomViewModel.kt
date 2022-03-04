package com.example.bookwhale.screen.chatroom

import android.util.Log
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

        return response.data!!.cachedIn(viewModelScope)
    }

    fun runStomp(roomId: Int, message: String){

        stompClient.topic("/sub/chat/room/${roomId}").subscribe { topicMessage ->
            Log.i("message Recieve", topicMessage.payload)
        }

        val headerList = arrayListOf<StompHeader>()
        headerList.add(StompHeader("roomId",roomId.toString()))
        headerList.add(StompHeader("senderId", myPreferenceManager.getId().toString()))
        headerList.add(StompHeader("senderIdentity", "seoplee"))
        headerList.add(StompHeader("content", "Hello, World!"))
        stompClient.connect(headerList)

        stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.i("OPEND", "!!")
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.i("CLOSED", "!!")

                }
                LifecycleEvent.Type.ERROR -> {
                    Log.i("ERROR", "!!")
                    Log.e("CONNECT ERROR", lifecycleEvent.exception.toString())
                }
                else ->{
                    Log.i("ELSE", lifecycleEvent.message)
                }
            }
        }

        val data = JSONObject()
        data.put("roomId", roomId.toString())
        data.put("senderId", myPreferenceManager.getId().toString())
        data.put("senderIdentity", "seoplee")
        data.put("content", "Hello, World!")

        stompClient.send("/pub/chat/message", data.toString()).subscribe()
    }
}
