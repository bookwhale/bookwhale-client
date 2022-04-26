package com.example.bookwhale.data.repository.article

import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class PostArticleRepositoryImpl(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher,
) : PostArticleRepository {
    override suspend fun postArticle(images: List<MultipartBody.Part>, articleRequest: ArticleDTO): NetworkResult<Boolean> = withContext(ioDispatcher) {

        val response = serverApiService.postArticle(images, articleRequest)

        if (response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }
}
