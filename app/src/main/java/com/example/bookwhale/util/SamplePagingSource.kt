package com.example.bookwhale.util

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.model.main.home.ArticleModel

class ArticlePagingSource(
    private val serverApiService: ServerApiService,
    private val search: String?
) : PagingSource<Int, ArticleModel>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, ArticleModel> {
        try {
            // Start refresh at page 1 if undefined.
            val next = params.key ?: 0
            val response = serverApiService.getAllArticles(search, next, 10)
            return LoadResult.Page(
                data = response.body()!!.map {
                    ArticleModel(
                        id = it.hashCode().toLong(),
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
                prevKey = if (next == 0) null else next - 1,
                nextKey = next + 1

            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleModel>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
