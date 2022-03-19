package com.example.bookwhale.screen.chatroom

sealed class ChatRoomState {

    object Uninitialized : ChatRoomState()

    object Loading : ChatRoomState()

    object Success : ChatRoomState()

    data class Error(
        val code : String?
    ) : ChatRoomState()

}