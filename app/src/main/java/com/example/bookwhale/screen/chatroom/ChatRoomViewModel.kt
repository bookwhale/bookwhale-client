package com.example.bookwhale.screen.chatroom

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.main.chat.ChatMessageModel
import com.example.bookwhale.model.MessageType
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ChatRoomViewModel(
    private val chatRepository: ChatRepository
): BaseViewModel() {

    val chatRoomState = MutableLiveData<ChatRoomState>(ChatRoomState.Uninitialized)
    val socketState = MutableLiveData<SocketState>(SocketState.Uninitialized)
    val articleTitle = MutableLiveData<String>()

    private val url = "ws://52.79.148.89:8081/ws/websocket"
    private val stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

    suspend fun getPreviousMessages(roomId: Int) : Flow<PagingData<ChatMessageModel>> {

        val response = chatRepository.getPreviousMessages(roomId)
        return response.data!!.cachedIn(viewModelScope)
    }

    suspend fun loadChatModel(roomId: Int) : ChatModel {

        chatRoomState.value = ChatRoomState.Loading

        var chatModel = ChatModel()

        viewModelScope.launch {
            val response = chatRepository.getChatList()

            if(response.status == NetworkResult.Status.SUCCESS) {
                chatRoomState.value = ChatRoomState.Success
                response.data!!.forEach {
                    if (it.roomId == roomId) {
                        chatModel = ChatModel(
                            id = it.hashCode().toLong(),
                            roomId = it.roomId,
                            articleId = it.articleId,
                            articleTitle = it.articleTitle,
                            articleImage = it.articleImage,
                            opponentProfile = it.opponentProfile,
                            opponentIdentity = it.opponentIdentity,
                            opponentDelete = it.opponentDelete,
                            lastContent = it.lastContent
                        )
                    }
                }
            } else {
                chatRoomState.value = ChatRoomState.Error(response.code)
            }
        }.join()

        return chatModel
    }

    @SuppressLint("CheckResult")
    fun runStomp(roomId: Int, message: String){

        stompClient.topic("/sub/chat/room/${roomId}").subscribe { topicMessage ->
            Log.i("message Recieve", topicMessage.payload)
            socketState.postValue(SocketState.MsgReceived)
        }

        val headerList = arrayListOf<StompHeader>()
        headerList.add(StompHeader("roomId",roomId.toString()))
        headerList.add(StompHeader("senderId", myPreferenceManager.getId().toString()))
        headerList.add(StompHeader("senderIdentity", myPreferenceManager.getName()))
        headerList.add(StompHeader("content", message))
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
                    socketState.value = SocketState.Error(lifecycleEvent.exception.toString())
                }
                else ->{
                    Log.i("ELSE", lifecycleEvent.message)
                }
            }
        }
    }

    fun sendMessage(roomId: Int, message: String) {
        val data = JSONObject()
        data.put("roomId", roomId.toString())
        data.put("senderId", myPreferenceManager.getId().toString())
        data.put("senderIdentity", myPreferenceManager.getName())
        data.put("content", message)

        stompClient.send("/pub/chat/message", data.toString()).subscribe()

        socketState.value = SocketState.MsgSend
    }
}
