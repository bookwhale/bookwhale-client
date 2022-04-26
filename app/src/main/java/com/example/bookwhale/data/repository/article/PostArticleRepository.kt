package com.example.bookwhale.data.repository.article

import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleDTO
import okhttp3.MultipartBody
interface PostArticleRepository {

    suspend fun postArticle(images: List<MultipartBody.Part>, articleRequest: ArticleDTO): NetworkResult<Boolean>
}
