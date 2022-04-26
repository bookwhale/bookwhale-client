package com.example.bookwhale.model.article

import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.Model

data class DetailImageModel(
    override val id: Long,
    override val type: CellType = CellType.DETAIL_IMAGE_LIST,
    var articleImage: String?
) : Model(id, type)
