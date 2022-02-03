package com.example.bookwhale.screen.main.home

import androidx.paging.PagingData
import com.example.bookwhale.data.entity.login.LoginEntity
import com.example.bookwhale.model.main.home.ArticleModel
import kotlinx.coroutines.flow.Flow

sealed class HomeState {

    object Uninitialized : HomeState()

    object Loading : HomeState()

    object Success : HomeState()

    data class Error(
        val code: String?
    ) : HomeState()

}