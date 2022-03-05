package com.example.bookwhale.screen.chatroom

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.databinding.ActivityChatRoomBinding
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.ChatPagingAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

import ua.naiksoftware.stomp.Stomp

class ChatRoomActivity : BaseActivity<ChatRoomViewModel, ActivityChatRoomBinding>() {

    override val viewModel by viewModel<ChatRoomViewModel>()

    override fun getViewBinding(): ActivityChatRoomBinding = ActivityChatRoomBinding.inflate(layoutInflater)

    private val chatModel by lazy { intent.getParcelableExtra<ChatModel>(CHAT_MODEL) }

    private val myPreferenceManager = object:
        KoinComponent {val myPreferenceManager: MyPreferenceManager by inject()}.myPreferenceManager

    private val url = "ws://52.79.148.89:8081/ws/websocket" // 소켓에 연결하는 엔드포인트가 /socket일때 다음과 같음
    private val stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

    private val adapter by lazy {
        ChatPagingAdapter(chatModel?.opponentProfile)
    }

    override fun initViews() {

        lifecycleScope.launch {
            runStomp(chatModel!!.roomId, getMessageText())
        }

        binding.recyclerView.adapter = adapter

        getMessages()

        initButtons()
    }

    private fun initButtons() = with(binding) {
        sendButton.setOnClickListener {
            sendMessage(chatModel!!.roomId, getMessageText())
        }
    }

    private fun getMessages() = with(binding) {
        chatModel?.let {
            lifecycleScope.launch {
                viewModel.getPreviousMessages(it.roomId).collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun runStomp(roomId: Int, message: String){

        stompClient.topic("/sub/chat/room/${roomId}").subscribe { topicMessage ->
            Log.i("message Recieve", topicMessage.payload)
            getMessages()
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
                }
                else ->{
                    Log.i("ELSE", lifecycleEvent.message)
                }
            }
        }
    }

    private fun getMessageText() : String = with(binding) {
        return editText.text.toString()
    }

    private fun sendMessage(roomId: Int, message: String) {
        val data = JSONObject()
        data.put("roomId", roomId.toString())
        data.put("senderId", myPreferenceManager.getId().toString())
        data.put("senderIdentity", myPreferenceManager.getName())
        data.put("content", message)

        stompClient.send("/pub/chat/message", data.toString()).subscribe()

        binding.editText.text.clear()
    }

    companion object {

        fun newIntent(context: Context, chatModel: ChatModel) = Intent(context, ChatRoomActivity::class.java).apply {
            putExtra(CHAT_MODEL, chatModel)
        }

        const val CHATROOM_ID = "0"
        const val CHAT_MODEL = "chatModel"
    }

    override fun observeData() {
        //
    }
}