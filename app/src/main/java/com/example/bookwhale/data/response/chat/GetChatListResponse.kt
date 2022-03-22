package com.example.bookwhale.data.response.chat

data class GetChatListResponse(
    val roomId: Int,
    val articleId : Int,
    val articleTitle : String,
    var articleImage : String?,
    val opponentIdentity : String,
    val opponentProfile : String?,
    val lastContent : String?,
    val opponentDelete: Boolean
)