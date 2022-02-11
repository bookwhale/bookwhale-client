package com.example.bookwhale.screen.article

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.favorite.AddFavoriteDTO
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.favorite.FavoriteState
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DetailArticleViewModel(
    private val detailRepository: DetailRepository,
    private val articleRepository: ArticleRepository
): BaseViewModel() {

    val detailArticleStateLiveData = MutableLiveData<DetailArticleState>(DetailArticleState.Uninitialized)
    fun loadArticle(articleId: Int) = viewModelScope.launch {

        detailArticleStateLiveData.value = DetailArticleState.Loading

        val articleResponse = detailRepository.getDetailArticle(articleId)
        val favoriteResponse = articleRepository.getFavoriteArticles()

        if (favoriteResponse.status == NetworkResult.Status.SUCCESS) {
            detailArticleStateLiveData.value = DetailArticleState.FavoriteSuccess(
                favoriteList = favoriteResponse.data!!.map {
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
            )
        } else {
            detailArticleStateLiveData.value = DetailArticleState.Error(favoriteResponse.code)
        }

        if(articleResponse.status == NetworkResult.Status.SUCCESS) {
            detailArticleStateLiveData.value = DetailArticleState.Success(
                articleResponse.data!!
            )
        } else {
            detailArticleStateLiveData.value = DetailArticleState.Error(articleResponse.code)
        }

    }


    fun addFavorite(articleId: Int) = viewModelScope.launch {
        articleRepository.addFavoriteArticle(AddFavoriteDTO(
            articleId = articleId
        ))

    }

    fun deleteFavorite(articleId: Int) = viewModelScope.launch {
        articleRepository.deleteFavoriteArticle(articleId)
    }

}