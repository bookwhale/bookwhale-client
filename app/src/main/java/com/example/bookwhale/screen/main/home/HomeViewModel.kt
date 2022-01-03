package com.example.bookwhale.screen.main.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.repository.main.home.HomeRepository
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository
): BaseViewModel() {

    val articleListLiveData = MutableLiveData<List<ArticleModel>>()

    fun getArticles(search: String? = null, page: Int, size: Int) = viewModelScope.launch {
        val response = homeRepository.getAllArticles("1", page, size)

        Log.e("response",response.toString())

        articleListLiveData.value = response?.map {
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
        }
    }
}