package com.example.bookwhale.data.response.chat

data class GetChatMessageResponse (
    val senderId: Int,
    val senderIdentity: String,
    val content: String,
    val createdDate: String
)