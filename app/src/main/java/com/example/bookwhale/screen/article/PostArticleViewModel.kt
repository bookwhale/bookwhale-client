package com.example.bookwhale.screen.article

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.article.PostArticleRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleDTO
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.home.HomeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PostArticleViewModel(
    private val postArticleRepository: PostArticleRepository

): BaseViewModel() {

    fun uploadArticle(files: List<MultipartBody.Part>, articleDTO: ArticleDTO) = viewModelScope.launch {

        val response = postArticleRepository.postArticle(files, articleDTO)
    }

}