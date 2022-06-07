package com.example.bookwhale.screen.article

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.article.ModifyArticleRepository
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleStatusCategory
import com.example.bookwhale.data.response.article.ArticleStatusDTO
import com.example.bookwhale.data.response.chat.MakeChatDTO
import com.example.bookwhale.data.response.favorite.AddFavoriteDTO
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.EventBus
import com.example.bookwhale.util.Events
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DetailArticleViewModel(
    private val detailRepository: DetailRepository,
    private val articleRepository: ArticleRepository,
    private val chatRepository: ChatRepository,
    private val modifyArticleRepository: ModifyArticleRepository,
    private val eventBus: EventBus
) : BaseViewModel() {

    val detailArticleStateLiveData = MutableLiveData<DetailArticleState>(DetailArticleState.Uninitialized)
    private val detailLoadFavoriteLiveData = MutableLiveData<DetailArticleState>(DetailArticleState.Uninitialized)
    private val loadChatListLiveData = MutableLiveData<Boolean>(false) // true = 이미 존재하는 채팅방
    val roomId = MutableLiveData<Int>(0)
    private var _articleId: Int = 0

    fun loadArticle(articleId: Int) = viewModelScope.launch {

        detailArticleStateLiveData.value = DetailArticleState.Loading

        _articleId = articleId

        val articleResponse = detailRepository.getDetailArticle(articleId)

        if (articleResponse.status == NetworkResult.Status.SUCCESS) {
            detailArticleStateLiveData.value = DetailArticleState.Success(
                articleResponse.data!!
            )
        } else {
            detailArticleStateLiveData.value = DetailArticleState.Error(articleResponse.code)
        }
    }

    fun loadFavorites() = viewModelScope.launch {

        detailLoadFavoriteLiveData.value = DetailArticleState.Loading

        val favoriteResponse = articleRepository.getFavoriteArticles()

        if (favoriteResponse.status == NetworkResult.Status.SUCCESS) {
            detailLoadFavoriteLiveData.value = DetailArticleState.FavoriteSuccess(
                favoriteList = favoriteResponse.data!!.map {
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
            )
        } else {
            detailLoadFavoriteLiveData.value = DetailArticleState.Error(favoriteResponse.code)
        }
    }

    fun addFavoriteAsync(articleId: Int): Deferred<Int> = viewModelScope.async {
        val response = articleRepository.addFavoriteArticle(
            AddFavoriteDTO(
                articleId = articleId
            )
        )

        if (response.status == NetworkResult.Status.SUCCESS) {
            response.data!!
        } else {
            0
        }
    }

    fun deleteFavoriteAsync(articleId: Int): Deferred<Boolean> = viewModelScope.async {
        val response = articleRepository.deleteFavoriteArticle(articleId)

        response.status == NetworkResult.Status.SUCCESS
    }

    fun makeNewChat(makeChatDTO: MakeChatDTO) = viewModelScope.launch {
        val isExist = loadChatRoomList(makeChatDTO.articleId)
        if (!isExist) {
            chatRepository.makeNewChat(makeChatDTO)
            loadChatListLiveData.value = false
            loadChatRoomList(makeChatDTO.articleId)
        } else {
            loadChatListLiveData.value = true
        }
    }

    fun deleteArticle(articleId: Int) = viewModelScope.launch {
        val response = modifyArticleRepository.deleteArticle(articleId)

        if (response.status == NetworkResult.Status.SUCCESS) {
            eventBus.produceEvent(Events.Deleted)
        } else {
            eventBus.produceEvent(Events.DeleteFail)
        }
    }

    fun updateStatus(articleId: Int, category: ArticleStatusCategory) = viewModelScope.launch {
        val response = modifyArticleRepository.updateStatus(articleId, ArticleStatusDTO(category.status))

        if (response.status == NetworkResult.Status.SUCCESS) {
            when (category) {
                ArticleStatusCategory.RESERVED -> {
                    eventBus.produceEvent(Events.Reserved)
                }
                ArticleStatusCategory.SOLD_OUT -> {
                    eventBus.produceEvent(Events.Sold)
                }
                ArticleStatusCategory.SALE -> {
                    eventBus.produceEvent(Events.Sale)
                }
            }
        } else {
            eventBus.produceEvent(Events.DeleteFail)
        }
    }

    private suspend fun loadChatRoomList(articleId: Int): Boolean = viewModelScope.async {
        val response = chatRepository.getChatList()

        if (response.status == NetworkResult.Status.SUCCESS) {
            val chatList = response.data!!
            chatList.forEach {
                if (it.articleId == articleId) {
                    roomId.value = it.roomId
                    return@async true
                }
            }
        } else {
            return@async false
        }

        return@async false
    }.await()
}
