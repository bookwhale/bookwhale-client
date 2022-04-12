package com.example.bookwhale.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex

class MessageChannel {

    val channel = Channel<String>()
    val mutex = Mutex()

    suspend fun sendToChannel(message: String?) {
        if (message != null) {
            channel.send(message)
        }
    }
}