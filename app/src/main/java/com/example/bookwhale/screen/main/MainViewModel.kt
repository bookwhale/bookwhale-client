package com.example.bookwhale.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.repository.my.MyRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.article.DetailArticleState
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.chat.ChatState
import com.example.bookwhale.screen.main.favorite.FavoriteState
import com.example.bookwhale.screen.main.home.HomeState
import com.example.bookwhale.screen.main.mypost.MyPostState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val articleRepository: ArticleRepository,
    private val myRepository: MyRepository,
    private val chatRepository: ChatRepository
): BaseViewModel() {

    private val _titleLiveData = MutableLiveData<String>()
    val titleLiveData : LiveData<String> = _titleLiveData

    private val _messageLiveData = MutableLiveData<String>()
    val messageLiveData : LiveData<String> = _messageLiveData

    private val _roomIdLiveData = MutableLiveData<String>()
    val roomIdLiveData : LiveData<String> = _roomIdLiveData

    val homeArticleStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)
    val favoriteArticleStateLiveData = MutableLiveData<FavoriteState>(FavoriteState.Uninitialized)
    val myArticleStateLiveData = MutableLiveData<MyPostState>(MyPostState.Uninitialized)
    val chatStateLiveData = MutableLiveData<ChatState>(ChatState.Uninitialized)

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
                    articleId = it.articleModel.articleId,
                    articleImage = it.articleModel.articleImage,
                    articleTitle = it.articleModel.articleTitle,
                    articlePrice = it.articleModel.articlePrice,
                    bookStatus = it.articleModel.bookStatus,
                    sellingLocation = it.articleModel.sellingLocation,
                    chatCount = it.articleModel.chatCount,
                    favoriteCount = it.articleModel.favoriteCount,
                    beforeTime = it.articleModel.beforeTime
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

    fun loadChatList() = viewModelScope.launch {
        chatStateLiveData.value = ChatState.Loading

        val response = chatRepository.getChatList()

        if(response.status == NetworkResult.Status.SUCCESS) {
            val chatList = response.data!!

            chatStateLiveData.value = ChatState.Success(chatList)
        } else {
            chatStateLiveData.value = ChatState.Error(
                response.code
            )
        }
    }

    fun loadPopupData() {
       _titleLiveData.value = myPreferenceManager.getTitle()
       _messageLiveData.value = myPreferenceManager.getMessage()
        _roomIdLiveData.value = myPreferenceManager.getRoomId()
    }

}
