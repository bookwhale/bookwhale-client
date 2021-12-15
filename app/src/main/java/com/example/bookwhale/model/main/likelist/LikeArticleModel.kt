package com.example.bookwhale.model.main.likelist

import com.example.bookwhale.data.entity.ArticleEntity
import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.Model

data class LikeArticleModel(
    override val id: Long,
    override val type: CellType = CellType.LIKE_LIST,
    val likeId : Long,
    val postResponse : ArticleEntity
) : Model(id, type)