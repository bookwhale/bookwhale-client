package com.example.bookwhale.data.repository.article

import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ModifyArticleDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody

class ModifyArticleRepositoryImpl (
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher,
): ModifyArticleRepository {
        override suspend fun modifyArticle(articleId : Int ,images: List<MultipartBody.Part>, articleUpdaterequest: ModifyArticleDTO): NetworkResult<Boolean> = withContext(ioDispatcher) {
            val response = serverApiService.modifyArticle(articleId, images, articleUpdaterequest)

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