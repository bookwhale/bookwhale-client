package com.example.bookwhale.screen.main.chat

import com.example.bookwhale.model.main.chat.ChatModel

sealed class ChatState {

    object Uninitialized : ChatState()

    object Loading : ChatState()

    data class Success(
        val chatList: List<ChatModel>
    ) : ChatState()

    data class Error(
        val code: String?
    ) : ChatState()
}
