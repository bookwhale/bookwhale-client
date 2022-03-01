package com.example.bookwhale.model.main.chat

data class ChatMessageModel (
    val type: MessageType = MessageType.OPPONENT,
    val senderId: Int,
    val senderIdentity: String,
    val content: String,
    val createdDate: String
)