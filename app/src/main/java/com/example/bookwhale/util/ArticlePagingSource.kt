package com.example.bookwhale.util

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.data.response.home.GetAllArticlesResponse
import com.example.bookwhale.model.main.home.ArticleModel

class ArticlePagingSource(
    private val serverApiService: ServerApiService,
    private val search: String?
) : PagingSource<Int, ArticleModel>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, ArticleModel> {
        return try {
            val next = params.key ?: 0
            val response = serverApiService.getAllArticles(search, next, 30).body()!!.map {
                ArticleModel(
                    id = it.articleId.toLong(),
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
            }
            LoadResult.Page(
                data = response,
                prevKey = if (next == 0) null else next - 1,
                nextKey = if (response.isEmpty()) null else next + 1 // response에 데이터가 없을 경우 다음페이지를 호출하지 않는다.
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}
