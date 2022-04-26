package com.example.bookwhale.data.response.article

data class ModifyArticleDTO(
    val title: String,
    val price: String,
    val description: String,
    val bookStatus: String,
    val sellingLocation: String,
    val deleteImgUrls: ArrayList<String>?
)
