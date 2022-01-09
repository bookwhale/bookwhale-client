package com.example.bookwhale.screen.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.favorite.FavoriteState
import com.example.bookwhale.screen.main.home.HomeState
import kotlinx.coroutines.launch

class MainViewModel(
    private val articleRepository: ArticleRepository
): BaseViewModel() {

    val articleListLiveData = MutableLiveData<List<ArticleModel>>()
    val favoriteListLiveData = MutableLiveData<List<FavoriteModel>>()

    val homeArticleStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)
    val favoriteArticleStateLiveData = MutableLiveData<FavoriteState>(FavoriteState.Uninitialized)

    fun getArticles(search: String? = null, page: Int, size: Int) = viewModelScope.launch {
        val response = articleRepository.getAllArticles(search, page, size)

        homeArticleStateLiveData.value = HomeState.Loading

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
        articleRepository.getLocalArticles()?.let {
            homeArticleStateLiveData.value = HomeState.Success(
                it.map { article ->
                    ArticleModel(
                        id = article.hashCode().toLong(),
                        articleId = article.articleId,
                        articleImage = article.articleImage,
                        articleTitle = article.articleTitle,
                        articlePrice = article.articlePrice,
                        bookStatus = article.bookStatus,
                        sellingLocation = article.sellingLocation,
                        chatCount = article.chatCount,
                        favoriteCount = article.favoriteCount,
                        beforeTime = article.beforeTime
                    )
                }
            )
        } ?: kotlin.run {
            homeArticleStateLiveData.value = HomeState.Error
        }

        // 내부 db에서 값을 꺼내서 보여준다.
//        articleListLiveData.value = articleRepository.getLocalArticles()?.map {
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

    fun getFavorites() = viewModelScope.launch {
        val response = articleRepository.getFavoriteArticles()

        favoriteArticleStateLiveData.value = FavoriteState.Loading

        val favorites = response?.map {
            FavoriteModel(
                id = it.hashCode().toLong(),
                favoriteId = it.favoriteId,
                articleId = it.articleEntity.articleId,
                articleImage = it.articleEntity.articleImage,
                articleTitle = it.articleEntity.articleTitle,
                articlePrice = it.articleEntity.articlePrice,
                bookStatus = it.articleEntity.bookStatus,
                sellingLocation = it.articleEntity.sellingLocation,
                chatCount = it.articleEntity.chatCount,
                favoriteCount = it.articleEntity.favoriteCount,
                beforeTime = it.articleEntity.beforeTime
            )
        }

        favorites?.let {
            favoriteArticleStateLiveData.value = FavoriteState.Success(it)
        } ?: kotlin.run {
            favoriteArticleStateLiveData.value = FavoriteState.Error
        }

        Log.e("favoriteList", favorites.toString())
    }
}
