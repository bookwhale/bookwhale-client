package com.example.bookwhale.util.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.network.ServerApiService
import java.lang.Exception

class MainPagingSource(
    private val service: ServerApiService
) : PagingSource<Int, ArticleEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ArticleEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleEntity> {
        return try {
            val next = params.key ?: 0
            val response = service.getAllArticles(null, next, 10)

            LoadResult.Page(
                data = response.body()!!.map {
                    ArticleEntity(
                        articleId = it.articleId,
                        articleImage = it.articleImage,
                        articleTitle = it.articleTitle,
                        articlePrice = it.articlePrice,
                        bookStatus = it.bookStatus,
                        sellingLocation = it.sellingLocation,
                        chatCount = it.chatCount,
                        favoriteCount = it.favoriteCount,
                        beforeTime = it.beforeTime
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