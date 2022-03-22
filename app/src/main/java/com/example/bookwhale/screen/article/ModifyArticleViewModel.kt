package com.example.bookwhale.screen.article

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookwhale.data.repository.article.DetailRepository
import com.example.bookwhale.data.repository.article.ModifyArticleRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.data.response.article.ArticleDTO
import com.example.bookwhale.data.response.article.ModifyArticleDTO
import com.example.bookwhale.data.response.login.TokenRequestDTO
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

class ModifyArticleViewModel (
    private val ModifyArticleRepository: ModifyArticleRepository,
    private val detailRepository: DetailRepository
    ): BaseViewModel() {

        val modifyArticleStateLiveData = MutableLiveData<ModifyArticleState>(ModifyArticleState.Uninitialized)


        fun loadArticle(articleId: Int) = viewModelScope.launch {

            modifyArticleStateLiveData.value = ModifyArticleState.Loading

            val articleResponse = detailRepository.getDetailArticle(articleId)

            if(articleResponse.status == NetworkResult.Status.SUCCESS) {
                modifyArticleStateLiveData.value = ModifyArticleState.LoadSuccess(
                    articleResponse.data!!
                )
            } else {
                modifyArticleStateLiveData.value = ModifyArticleState.Error(articleResponse.code)
            }
        }//이걸보고 detail 쓰는것을 참고하자 정보가져오기

        fun uploadArticle(files: List<MultipartBody.Part>, articleDTO: ModifyArticleDTO) = viewModelScope.launch {

            modifyArticleStateLiveData.value = ModifyArticleState.Loading
            val response = ModifyArticleRepository.modifyArticle(files, articleDTO)
            Log.e("modifyArticle",articleDTO.toString()+files.toString())

            if(response.status == NetworkResult.Status.SUCCESS) {
                modifyArticleStateLiveData.value = ModifyArticleState.ModifySuccess
            } else {
                modifyArticleStateLiveData.value = ModifyArticleState.Error(
                    code = response.code
                )
            }
        }
}