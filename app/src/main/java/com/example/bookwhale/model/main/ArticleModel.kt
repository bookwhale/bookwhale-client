package com.example.bookwhale.model.main

import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.Model

data class ArticleModel(
    override val id: Long,
    override val type: CellType = CellType.ARTICLE_LIST,
    val postId : Long,
    val postImage : String,
    val postTitle : String,
    val postPrice : String,
    val postStatus: String,
    val bookTitle: String,
    val bookAuthor: String,
    val bookPublisher : String,
    val sellingLocation : String,
    val viewCount : Long,
    val likeCount : Long,
    val beforeTime : String
) : Model(id, type)