package com.example.bookwhale.data.repository.article

import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.article.DetailArticleModel
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.model.main.chat.ChatModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultDetailRepository(
    private val serverApiService: ServerApiService,
    private val ioDispatcher: CoroutineDispatcher
) : DetailRepository {
    override suspend fun getDetailArticle(articleId: Int): NetworkResult<DetailArticleModel> = withContext(ioDispatcher) {
        val response = serverApiService.getDetailArticle(articleId)

        if(response.isSuccessful) {
            NetworkResult.success(
                DetailArticleModel(
                    sellerId = response.body()!!.sellerId,
                    sellerIdentity = response.body()!!.sellerIdentity,
                    sellerProfileImage = response.body()!!.sellerProfileImage,
                    articleId = response.body()!!.articleId,
                    title = response.body()!!.title,
                    price = response.body()!!.price,
                    description = response.body()!!.description,
                    bookStatus = response.body()!!.bookStatus,
                    articleStatus = response.body()!!.articleStatus,
                    sellingLocation = response.body()!!.sellingLocation,
                    images = response.body()!!.images.map {
                        DetailImageModel(
                            id = it.hashCode().toLong(),
                            articleImage = it
                        )
                    },
                    bookResponse = response.body()!!.bookResponse,
                    viewCount = response.body()!!.viewCount,
                    favoriteCount = response.body()!!.favoriteCount,
                    beforeTime = response.body()!!.beforeTime,
                    myArticle = response.body()!!.myArticle,
                    myFavorite = response.body()!!.myFavorite
                )
            )
        } else {
            val errorCode = ErrorConverter.convert(response.errorBody()?.string())
            NetworkResult.error(code = errorCode)
        }
    }
}