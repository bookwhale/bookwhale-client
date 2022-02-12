package com.example.bookwhale.data.repository.article

import androidx.paging.PagingData
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.article.DetailArticleModel
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.model.main.home.ArticleModel
import kotlinx.coroutines.flow.Flow

interface DetailRepository {

    suspend fun getDetailArticle(articleId: Int) : NetworkResult<DetailArticleModel>

    suspend fun getNaverBookApi(title: String) : NetworkResult<Flow<PagingData<NaverBookModel>>>

}