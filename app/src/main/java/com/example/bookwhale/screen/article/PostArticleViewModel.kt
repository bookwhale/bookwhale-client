package com.example.bookwhale.screen.article

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.article.PostArticleRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleDTO
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class PostArticleViewModel(
    private val postArticleRepository: PostArticleRepository
): BaseViewModel() {

    val postArticleStateLiveData = MutableLiveData<PostArticleState>(PostArticleState.Uninitialized)

    fun uploadArticle(files: List<MultipartBody.Part>, articleDTO: ArticleDTO) = viewModelScope.launch {

        postArticleStateLiveData.value = PostArticleState.Loading
        val response = postArticleRepository.postArticle(files, articleDTO)

        if(response.status == NetworkResult.Status.SUCCESS) {
            postArticleStateLiveData.value = PostArticleState.Success
        } else {
            postArticleStateLiveData.value = PostArticleState.Error(
                code = response.code
            )
        }
    }

}