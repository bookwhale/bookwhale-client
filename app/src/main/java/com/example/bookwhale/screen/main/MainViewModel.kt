package com.example.bookwhale.screen.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.repository.my.MyRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.model.main.my.NotiModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.chat.ChatState
import com.example.bookwhale.screen.main.favorite.FavoriteState
import com.example.bookwhale.screen.main.home.HomeState
import com.example.bookwhale.screen.main.mypost.MyPostState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(
    private val articleRepository: ArticleRepository,
    private val myRepository: MyRepository,
    private val chatRepository: ChatRepository
) : BaseViewModel() {

    private val _notiSettingLiveData = MutableLiveData<NotiModel>()
    val notiSettingLiveData: LiveData<NotiModel> = _notiSettingLiveData

    val homeArticleStateLiveData = MutableLiveData<HomeState>(HomeState.Uninitialized)
    val favoriteArticleStateLiveData = MutableLiveData<FavoriteState>(FavoriteState.Uninitialized)
    val myArticleStateLiveData = MutableLiveData<MyPostState>(MyPostState.Uninitialized)
    val chatStateLiveData = MutableLiveData<ChatState>(ChatState.Uninitialized)

    var favoriteList: List<FavoriteModel>? = null
    var myArticleList: List<ArticleModel>? = null

    init {
        myPreferenceManager.removeSocketStatus()
        myPreferenceManager.removeRoomId()

        getMyInfo()
        getNotiSetting()
    }

    suspend fun getArticlesPaging(search: String? = null): Flow<PagingData<ArticleModel>> {
        homeArticleStateLiveData.value = HomeState.Loading

        val response = articleRepository.getAllArticlesPaging(search)

        if (response.status == NetworkResult.Status.SUCCESS) homeArticleStateLiveData.value = HomeState.Success
        else HomeState.Error(response.code)

        return response.data!!.cachedIn(viewModelScope)
    }

    fun getFavorites() = viewModelScope.launch {
        favoriteArticleStateLiveData.value = FavoriteState.Loading

        val response = articleRepository.getFavoriteArticles()

        if (response.status == NetworkResult.Status.SUCCESS) {
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

        if (response.status == NetworkResult.Status.SUCCESS) {
            myArticleList = response.data
            myArticleStateLiveData.value = MyPostState.Success(myArticleList!!)
        } else {
            myArticleStateLiveData.value = MyPostState.Error(response.code)
        }
    }

    fun getMyInfo() = viewModelScope.launch {
        val response = myRepository.getMyInfo()

        if (response.status == NetworkResult.Status.SUCCESS) {
            myPreferenceManager.putId(response.data!!.userId)
            myPreferenceManager.putName(response.data!!.nickName)
        } else {
            Log.e("GetMyInfo Error: ", "${response.code}")
        }
    }

    fun loadChatList() = viewModelScope.launch {
        chatStateLiveData.value = ChatState.Loading

        val response = chatRepository.getChatList()

        if (response.status == NetworkResult.Status.SUCCESS) {
            val chatList = response.data!!
            chatStateLiveData.value = ChatState.Success(chatList)
        } else {
            chatStateLiveData.value = ChatState.Error(
                response.code
            )
        }
    }

    fun getNotiSetting() = viewModelScope.launch {
        val response = myRepository.getNotiSetting()

        if (response.status == NetworkResult.Status.SUCCESS) {
            _notiSettingLiveData.value = response.data!!
        } else {
            Log.e("Get Noti Error", "${response.code}")
        }
    }

    fun toggleNotiSetting() = viewModelScope.launch {
        val response = myRepository.toggleNotiSetting()

        if (response.status == NetworkResult.Status.SUCCESS) {
            Log.i("Noti Toggle : ", "Success")
        } else {
            Log.e("Get Noti Error", "${response.code}")
        }
    }
}
