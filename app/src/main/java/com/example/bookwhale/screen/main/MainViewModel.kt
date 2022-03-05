package com.example.bookwhale.screen.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.bookwhale.data.preference.MyPreferenceManager
import com.example.bookwhale.data.repository.login.LoginRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.repository.my.MyRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.favorite.AddFavoriteDTO
import com.example.bookwhale.data.response.login.TokenRequestDTO
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.favorite.FavoriteState
import com.example.bookwhale.screen.main.home.HomeState
import com.example.bookwhale.screen.main.mypost.MyPostState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(
    private val articleRepository: ArticleRepository,
    private val myRepository: MyRepository
): BaseViewModel() {

    val homeArticleStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)
    val favoriteArticleStateLiveData = MutableLiveData<FavoriteState>(FavoriteState.Uninitialized)
    val myArticleStateLiveData = MutableLiveData<MyPostState>(MyPostState.Uninitialized)

    var favoriteList : List<FavoriteModel>? = null
    var myArticleList : List<ArticleModel>? = null

    suspend fun getArticlesPaging(search: String? = null) : Flow<PagingData<ArticleModel>> {
        homeArticleStateLiveData.value = HomeState.Loading

        val response = articleRepository.getAllArticlesPaging(search)

        if(response.status == NetworkResult.Status.SUCCESS) homeArticleStateLiveData.value = HomeState.Success
        else HomeState.Error(response.code)

        return response.data!!.cachedIn(viewModelScope)

    }

    fun getFavorites() = viewModelScope.launch {
        favoriteArticleStateLiveData.value = FavoriteState.Loading

        val response = articleRepository.getFavoriteArticles()

        if(response.status == NetworkResult.Status.SUCCESS) {
            favoriteList = response.data?.map {
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
            favoriteArticleStateLiveData.value = FavoriteState.Success(favoriteList!!)
        } else {
            favoriteArticleStateLiveData.value = FavoriteState.Error(response.code)
        }
    }

    fun getMyArticles() = viewModelScope.launch {
        myArticleStateLiveData.value = MyPostState.Loading

        val response = articleRepository.getMyArticles()

        if(response.status == NetworkResult.Status.SUCCESS) {
            myArticleList = response.data
            myArticleStateLiveData.value = MyPostState.Success(myArticleList!!)
        } else {
            myArticleStateLiveData.value = MyPostState.Error(response.code)
        }
    }

    fun getMyInfo() = viewModelScope.launch {
        val response = myRepository.getMyInfo()

        if(response.status == NetworkResult.Status.SUCCESS) {
            myPreferenceManager.putId(response.data!!.userId)
            myPreferenceManager.putName(response.data!!.nickName)
        } else {

        }
    }
}
