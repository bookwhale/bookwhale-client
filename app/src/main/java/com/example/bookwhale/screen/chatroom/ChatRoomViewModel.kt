package com.example.bookwhale.screen.chatroom

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.model.main.chat.ChatMessageModel
import com.example.bookwhale.model.MessageType
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp

class ChatRoomViewModel(
    private val chatRepository: ChatRepository,
    private val detailRepository: DetailRepository

): BaseViewModel() {

    val articleTitle = MutableLiveData<String>()

    suspend fun getPreviousMessages(roomId: Int) : Flow<PagingData<ChatMessageModel>> {

        val response = chatRepository.getPreviousMessages(roomId)
        return response.data!!.cachedIn(viewModelScope)
    }

    fun loadArticleAsync(articleId: Int) = viewModelScope.launch {

        articleTitle.value = detailRepository.getDetailArticle(articleId).data!!.title
    }
}
