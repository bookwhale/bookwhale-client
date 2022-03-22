package com.example.bookwhale.data.repository.article

import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleDTO
import com.example.bookwhale.data.response.article.ModifyArticleDTO
import okhttp3.MultipartBody

interface ModifyArticleRepository {

    suspend fun modifyArticle(images: List<MultipartBody.Part>, articleUpdaterequest: ModifyArticleDTO) : NetworkResult<Boolean>

}