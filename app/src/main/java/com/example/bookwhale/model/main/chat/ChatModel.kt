package com.example.bookwhale.model.main.chat

import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.Model

data class ChatModel(
    override val id: Long,
    override val type: CellType = CellType.CHAT_LIST,
    val roomId: Int,
    val articleId : Int,
    var articleImage : String?,
    val opponentIdentity : String,
    val opponentProfile : String,
    val opponentDelete: Boolean
) : Model(id, type)