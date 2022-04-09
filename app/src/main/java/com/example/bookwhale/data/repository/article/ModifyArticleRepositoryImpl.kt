package com.example.bookwhale.data.repository.article

import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleStatusDTO
import com.example.bookwhale.data.response.article.ModifyArticleDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class ModifyArticleRepositoryImpl (
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher,
): ModifyArticleRepository {
        override suspend fun modifyArticle(articleId : Int ,images: List<MultipartBody.Part>, articleUpdateRequest: ModifyArticleDTO): NetworkResult<Boolean> = withContext(ioDispatcher) {
            val response = serverApiService.modifyArticle(articleId, images, articleUpdateRequest)

            if(response.isSuccessful) {
                NetworkResult.success(
                    true
                )
            } else {
                val errorCode = ErrorConverter.convert(response.errorBody()?.string())
                NetworkResult.error(code = errorCode)
            }
        }

    override suspend fun deleteArticle(articleId: Int) : NetworkResult<Boolean> = withContext(ioDispatcher) {
        val response = serverApiService.deleteArticle(articleId)

        if(response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }

    override suspend fun updateStatus(articleId: Int, articleStatus: ArticleStatusDTO) : NetworkResult<Boolean> = withContext(ioDispatcher) {
        val response = serverApiService.updateArticleStatus(articleId, articleStatus)

        if(response.isSuccessful) {
            NetworkResult.success(
                true
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }
}