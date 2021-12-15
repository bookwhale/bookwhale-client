package com.example.bookwhale.screen.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.model.main.ArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(
    private val articleRepository: ArticleRepository
): BaseViewModel() {

    val articleListLiveData = MutableLiveData<List<ArticleModel>>()

    override fun fetchData() = viewModelScope.launch {

        articleListLiveData.value = articleRepository.getArticleList(0, 10).map {
            ArticleModel(
                id = it.hashCode().toLong(),
                postId = it.postId,
                postImage = it.postImage,
                postTitle = it.postTitle,
                postPrice = it.postPrice,
                postStatus = it.postStatus,
                bookTitle = it.bookTitle,
                bookAuthor = it.bookAuthor,
                bookPublisher = it.bookPublisher,
                sellingLocation = it.sellingLocation,
                viewCount = it.viewCount,
                likeCount = it.likeCount,
                beforeTime = it.beforeTime
            )
        }

    }
}
