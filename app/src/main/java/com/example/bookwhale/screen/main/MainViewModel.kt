package com.example.bookwhale.screen.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel(
    private val articleRepository: ArticleRepository
): BaseViewModel() {

    val articleListLiveData = MutableLiveData<List<ArticleModel>>()

    fun getArticles(search: String? = null, page: Int, size: Int) = viewModelScope.launch {
        val response = articleRepository.getAllArticles(search, page, size)

        // 내부 db에 네트워크를 통해 가져온값을 넣는다.
        response?.forEach {
            articleRepository.insertLocalArticles(ArticleEntity(
                articleId = it.articleId,
                articleImage = it.articleImage,
                articleTitle = it.articleTitle,
                articlePrice = it.articlePrice,
                bookStatus = it.bookStatus,
                sellingLocation = it.sellingLocation,
                chatCount = it.chatCount,
                favoriteCount = it.favoriteCount,
                beforeTime = it.beforeTime
            ))
        }

        // 내부 db에서 값을 꺼내서 보여준다.
        articleListLiveData.value = articleRepository.getLocalArticles()?.map {
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

//        articleListLiveData.value?.forEach {
//            homeRepository.insertLocalArticles(GetAllArticleEntity(
//                articleId = it.articleId,
//                articleImage = it.articleImage,
//                articleTitle = it.articleTitle,
//                articlePrice = it.articlePrice,
//                bookStatus = it.bookStatus,
//                sellingLocation = it.sellingLocation,
//                chatCount = it.chatCount,
//                favoriteCount = it.favoriteCount,
//                beforeTime = it.beforeTime
//            ))
//        }
//
//        articleListLiveData.value = response?.map {
//            ArticleModel(
//                id = it.hashCode().toLong(),
//                articleId = it.articleId,
//                articleImage = it.articleImage,
//                articleTitle = it.articleTitle,
//                articlePrice = it.articlePrice,
//                bookStatus = it.bookStatus,
//                sellingLocation = it.sellingLocation,
//                chatCount = it.chatCount,
//                favoriteCount = it.favoriteCount,
//                beforeTime = it.beforeTime
//            )
//        }
//

        Log.e("localArticle?",articleRepository.getLocalArticles().toString())
    }
}
