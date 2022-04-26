package com.example.bookwhale.data.repository.article

import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleStatusDTO
import com.example.bookwhale.data.response.article.ModifyArticleDTO
import okhttp3.MultipartBody

interface ModifyArticleRepository {

    suspend fun modifyArticle(articleId: Int, images: List<MultipartBody.Part>, articleUpdateRequest: ModifyArticleDTO): NetworkResult<Boolean>

    suspend fun deleteArticle(articleId: Int): NetworkResult<Boolean>

    suspend fun updateStatus(articleId: Int, articleStatus: ArticleStatusDTO): NetworkResult<Boolean>
}
