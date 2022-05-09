package com.example.bookwhale.screen.chatroom

sealed class ChatRoomState {

    object Uninitialized : ChatRoomState()

    object Loading : ChatRoomState()

    object Success : ChatRoomState()

    object Deleted : ChatRoomState()

    data class Error(
        val code: String?
    ) : ChatRoomState()
}
