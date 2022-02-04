package com.example.bookwhale.screen.main.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.home.HomeState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository
) : BaseViewModel() {

    val chatStateLiveData = MutableLiveData<ChatState>(ChatState.Uninitialized)

    override fun fetchData() = viewModelScope.launch {
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
}

//        if(response.status == NetworkResult.Status.SUCCESS) {
//
//            articleList = response.data!!.map {
//                ArticleModel(
//                    id = it.hashCode().toLong(),
//                    articleId = it.articleId,
//                    articleImage = it.articleImage,
//                    articleTitle = it.articleTitle,
//                    articlePrice = it.articlePrice,
//                    bookStatus = it.bookStatus,
//                    sellingLocation = it.sellingLocation,
//                    chatCount = it.chatCount,
//                    favoriteCount = it.favoriteCount,
//                    beforeTime = it.beforeTime
//                )
//            }
//            homeArticleStateLiveData.value = HomeState.Success(articleList!! as List<ArticleModel>)
//            Log.e("localArticleList",articleList.toString())
//        } else {
//            homeArticleStateLiveData.value = HomeState.Error(
//                response.code
//            )
//        }