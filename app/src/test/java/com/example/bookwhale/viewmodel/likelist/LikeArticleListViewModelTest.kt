package com.example.bookwhale.viewmodel.likelist

import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.screen.main.lielist.LikeListViewModel
import com.example.bookwhale.viewmodel.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.test.inject

@ExperimentalCoroutinesApi
internal class LikeArticleListViewModelTest: ViewModelTest() {

    private val articleRepository by inject<ArticleRepository>()
    private val likeListViewModel by inject<LikeListViewModel>()


}