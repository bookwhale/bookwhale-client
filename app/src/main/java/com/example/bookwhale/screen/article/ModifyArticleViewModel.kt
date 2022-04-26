package com.example.bookwhale.screen.article

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.article.ModifyArticleRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ModifyArticleDTO
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ModifyArticleViewModel(
    private val ModifyArticleRepository: ModifyArticleRepository,
    private val detailRepository: DetailRepository
) : BaseViewModel() {

    val modifyArticleStateLiveData = MutableLiveData<ModifyArticleState>(ModifyArticleState.Uninitialized)

    fun loadArticle(articleId: Int) = viewModelScope.launch {

        modifyArticleStateLiveData.value = ModifyArticleState.Loading

        val articleResponse = detailRepository.getDetailArticle(articleId)

        if (articleResponse.status == NetworkResult.Status.SUCCESS) {
            modifyArticleStateLiveData.value = ModifyArticleState.LoadSuccess(
                articleResponse.data!!
            )
        } else {
            modifyArticleStateLiveData.value = ModifyArticleState.Error(articleResponse.code)
        }
    }

    fun uploadArticle(articleId: Int, files: List<MultipartBody.Part>, articleDTO: ModifyArticleDTO) = viewModelScope.launch {

        modifyArticleStateLiveData.value = ModifyArticleState.Loading
        val response = ModifyArticleRepository.modifyArticle(articleId, files, articleDTO)

        if (response.status == NetworkResult.Status.SUCCESS) {
            modifyArticleStateLiveData.value = ModifyArticleState.ModifySuccess
        } else {
            modifyArticleStateLiveData.value = ModifyArticleState.Error(
                code = response.code
            )
        }
    }
}
