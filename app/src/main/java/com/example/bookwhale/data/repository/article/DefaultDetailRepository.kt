package com.example.bookwhale.data.repository.article

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.ErrorConverter
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.article.DetailArticleModel
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.util.NaverBookPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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
                    myFavoriteId = response.body()!!.myFavoriteId,
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

    override suspend fun getNaverBookApi(title: String): NetworkResult<Flow<PagingData<NaverBookModel>>> = withContext(ioDispatcher) {
        val response = Pager(
            PagingConfig(pageSize = 10)
        ) {
            NaverBookPagingSource(serverApiService, title)
        }.flow

        NetworkResult.success(
            response
        )

    }


}