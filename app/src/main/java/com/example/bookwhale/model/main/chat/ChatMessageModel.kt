package com.example.bookwhale.model.main.chat

import com.example.bookwhale.model.MessageType

data class ChatMessageModel(
    val type: MessageType = MessageType.OPPONENT,
    val senderId: Int,
    val senderIdentity: String,
    val content: String,
    val createdDate: String
)
