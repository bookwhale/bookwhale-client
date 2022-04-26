package com.example.bookwhale.screen.chatroom

sealed class SocketState {

    object Uninitialized : SocketState()

    object Loading : SocketState()

    object MsgReceived : SocketState()

    object MsgSend : SocketState()

    data class Error(
        val code: String?
    ) : SocketState()
}
