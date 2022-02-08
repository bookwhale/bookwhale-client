package com.example.bookwhale.screen.article

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailArticleViewModel(
    private val detailRepository: DetailRepository
): BaseViewModel() {

    val detailArticleStateLiveData = MutableLiveData<DetailArticleState>(DetailArticleState.Uninitialized)

    fun loadArticle(articleId: Int) = viewModelScope.launch {
        val response = detailRepository.getDetailArticle(articleId)

        if(response.status == NetworkResult.Status.SUCCESS) {
            Log.e("data", response.data!!.toString())
            detailArticleStateLiveData.value = DetailArticleState.Success(
                response.data!!
            )
        } else {

        }
    }
}