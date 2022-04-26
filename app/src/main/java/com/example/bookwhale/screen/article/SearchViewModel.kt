package com.example.bookwhale.screen.article

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class SearchViewModel(
    private val detailRepository: DetailRepository
) : BaseViewModel() {

    val searchStateLiveData = MutableLiveData<SearchState>(SearchState.Uninitialized)

    suspend fun getNaverBookPaging(title: String): Flow<PagingData<NaverBookModel>> {

        searchStateLiveData.value = SearchState.Loading

        val response = detailRepository.getNaverBookApi(title)

        if (response.status == NetworkResult.Status.SUCCESS) {
            searchStateLiveData.value = SearchState.Success
        } else {
            searchStateLiveData.value = SearchState.Error(response.code)
        }

        return response.data!!.cachedIn(viewModelScope)
    }
}
