package com.example.bookwhale.screen.main.lielist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.model.main.likelist.LikeArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LikeListViewModel(
    private val articleRepository: ArticleRepository
): BaseViewModel() {

    val likeArticleListLiveData = MutableLiveData<List<LikeArticleModel>>()

    override fun fetchData(): Job = viewModelScope.launch {

        val likeArticleList = articleRepository.getLikeArticleList()

        likeArticleListLiveData.value = likeArticleList.map {
            LikeArticleModel(
                id = it.hashCode().toLong(),
                likeId = it.likeId,
                postResponse = it.postResponse
            )
        }
    }

}