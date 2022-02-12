package com.example.bookwhale.screen.article

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class SearchViewModel(
    private val detailRepository: DetailRepository
):BaseViewModel() {
    suspend fun getNaverBookPaging(title: String) : Flow<PagingData<NaverBookModel>> {

        val response = detailRepository.getNaverBookApi(title)

        return response.data!!.cachedIn(viewModelScope)

    }
}