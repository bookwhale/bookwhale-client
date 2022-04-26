package com.example.bookwhale.widget.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookwhale.data.network.ServerApiService
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.util.DEFAULT_PAGING_CAPACITY
import com.example.bookwhale.util.NAVER_BOOK_PAGING_PARAM

class NaverBookPagingSource(
    private val serverApiService: ServerApiService,
    private val title: String
) : PagingSource<Int, NaverBookModel>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, NaverBookModel> {
        return try {
            val next = params.key ?: NAVER_BOOK_PAGING_PARAM
            val response = serverApiService.getNaverBookApi(title, DEFAULT_PAGING_CAPACITY, next).body()!!.map {
                NaverBookModel(
                    bookIsbn = it.bookIsbn,
                    bookTitle = it.bookTitle,
                    bookAuthor = it.bookAuthor,
                    bookPublisher = it.bookPublisher,
                    bookThumbnail = it.bookThumbnail,
                    bookListPrice = it.bookListPrice,
                    bookPubDate = it.bookPubDate,
                    bookSummary = it.bookSummary
                )
            }
            LoadResult.Page(
                data = response,
                prevKey = if (next == 1) null else next - 1,
                nextKey = if (response.isEmpty()) null else next + 1 // response에 데이터가 없을 경우 다음페이지를 호출하지 않는다.
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NaverBookModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
