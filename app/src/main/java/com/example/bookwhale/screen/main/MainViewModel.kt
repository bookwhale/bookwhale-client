package com.example.bookwhale.screen.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.repository.login.LoginRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.favorite.FavoriteState
import com.example.bookwhale.screen.main.home.HomeState
import kotlinx.coroutines.launch

class MainViewModel(
    private val articleRepository: ArticleRepository,
    private val loginRepository: LoginRepository,
    private val myPreferenceManager: MyPreferenceManager
): BaseViewModel() {

    val homeArticleStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)
    val favoriteArticleStateLiveData = MutableLiveData<FavoriteState>(FavoriteState.Uninitialized)

    fun getArticles(search: String? = null, page: Int, size: Int) = viewModelScope.launch {
        homeArticleStateLiveData.value = HomeState.Loading

        val response = articleRepository.getAllArticles(search, page, size)

        if(response.status == NetworkResult.Status.SUCCESS) {
            // 내부 db에 네트워크를 통해 가져온값을 넣는다.
            response.data?.forEach {
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
        } else {
            homeArticleStateLiveData.value = HomeState.Error(response.code)
        }


        // 내부 db에서 값을 꺼내서 보여준다.
        articleRepository.getLocalArticles().let {
            homeArticleStateLiveData.value = HomeState.Success(
                it.data!!.map { article ->
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

        if(response.status == NetworkResult.Status.SUCCESS) {
            val favorites = response.data?.map {
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
            favoriteArticleStateLiveData.value = FavoriteState.Success(favorites!!)
        } else {
            favoriteArticleStateLiveData.value = FavoriteState.Error(response.code)
        }

//        val favorites2 = response?.map {
//            FavoriteModel(
//                id = it.hashCode().toLong(),
//                favoriteId = it.favoriteId,
//                articleId = it.articleEntity.articleId,
//                articleImage = it.articleEntity.articleImage,
//                articleTitle = it.articleEntity.articleTitle,
//                articlePrice = it.articleEntity.articlePrice,
//                bookStatus = it.articleEntity.bookStatus,
//                sellingLocation = it.articleEntity.sellingLocation,
//                chatCount = it.articleEntity.chatCount,
//                favoriteCount = it.articleEntity.favoriteCount,
//                beforeTime = it.articleEntity.beforeTime
//            )
//        }
//
//        favorites?.let {
//            favoriteArticleStateLiveData.value = FavoriteState.Success(it)
//        } ?: kotlin.run {
//            favoriteArticleStateLiveData.value = FavoriteState.Error
//        }
//
//        Log.e("favoriteList", favorites.toString())
    }

    fun getNewTokens() = viewModelScope.launch {
        val response = loginRepository.getNewTokens(TokenRequestDTO(
            apiToken = myPreferenceManager.getAccessToken()!!,
            refreshToken = myPreferenceManager.getRefreshToken()!!
        ))

        response.apiToken?.let {
            myPreferenceManager.putAccessToken(response.apiToken)
            myPreferenceManager.putRefreshToken(response.refreshToken!!)
        }
    }
}
