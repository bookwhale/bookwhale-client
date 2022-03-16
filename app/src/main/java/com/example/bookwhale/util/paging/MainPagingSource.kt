package com.example.bookwhale.util.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.model.main.home.ArticleModel
import java.lang.Exception

class MainPagingSource(
    private val service: ServerApiService
) : PagingSource<Int, ArticleModel>() {

    override fun getRefreshKey(state: PagingState<Int, ArticleModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleModel> {
        return try {
            val next = params.key ?: 0
            val response = service.getAllArticles(null, next, 10)

            LoadResult.Page(
                data = response.body()!!.mapIndexed { index, data ->
                    ArticleModel(
                        id = index.toLong(),
                        articleId = data.articleId,
                        articleImage = data.articleImage,
                        articleTitle = data.articleTitle,
                        articlePrice = data.articlePrice,
                        bookStatus = data.bookStatus,
                        sellingLocation = data.sellingLocation,
                        chatCount = data.chatCount,
                        favoriteCount = data.favoriteCount,
                        beforeTime = data.beforeTime
                    )
                },
                prevKey = if(next == 0) null else next - 1,
                nextKey = next + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}