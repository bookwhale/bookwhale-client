package com.example.bookwhale.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex

class MessageChannel {

    val channel = Channel<PopupMessage>(capacity = 1)

    suspend fun trySendToChannel(title: String?, message: String?, roomId: String?) {
        val data = PopupMessage(title = title, message = message, roomId = roomId)

        data.title?.let {
            channel.trySend(data)
        }
    }

    data class PopupMessage(
        val title : String?,
        val message : String?,
        val roomId : String?
    )
}