package com.example.bookwhale.model.main.chat

import android.os.Parcelable
import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.Model
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatModel(
    override val id: Long,
    override val type: CellType = CellType.CHAT_LIST,
    val roomId: Int,
    val articleId : Int,
    val articleTitle : String,
    var articleImage : String?,
    val opponentIdentity : String,
    var opponentProfile : String?,
    val roomCreateAt : String?,
    var lastContent : String?,
    val lastContentCreateAt : String?,
    val opponentDelete: Boolean
) : Model(id, type), Parcelable {
    constructor(): this(0,CellType.CHAT_LIST,0,0,"","","","","","","",false)

}