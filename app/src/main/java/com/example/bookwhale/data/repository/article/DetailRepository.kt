package com.example.bookwhale.data.repository.article

import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.article.DetailArticleModel
import com.example.bookwhale.model.main.chat.ChatModel

interface DetailRepository {

    suspend fun getDetailArticle(articleId: Int) : NetworkResult<DetailArticleModel>

}