package com.example.bookwhale.model.main.home

import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.Model

data class ArticleModel(
    override val id: Long,
    override val type: CellType = CellType.ARTICLE_LIST,
    val articleId : Int,
    val articleImage : String,
    val articleTitle : String,
    val articlePrice : String,
    val bookStatus: String,
    val sellingLocation : String,
    val chatCount : Int,
    val favoriteCount : Int,
    val beforeTime : String
) : Model(id, type)