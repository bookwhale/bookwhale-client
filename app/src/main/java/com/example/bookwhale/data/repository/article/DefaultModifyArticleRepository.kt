package com.example.bookwhale.data.repository.article

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleDTO
import com.example.bookwhale.data.response.article.ModifyArticleDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URI
import java.net.URI.create


class DefaultModifyArticleRepository (

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