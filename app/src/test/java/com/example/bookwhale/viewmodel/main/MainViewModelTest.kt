package com.example.bookwhale.viewmodel.main

import com.example.bookwhale.data.entity.favorite.FavoriteEntity
import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.data.response.NetworkResult
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.screen.main.favorite.FavoriteState
import com.example.bookwhale.screen.main.home.HomeState
import com.example.bookwhale.viewmodel.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.test.inject

@ExperimentalCoroutinesApi
internal class MainViewModelTest: ViewModelTest() {

    private val articleRepository by inject<ArticleRepository>()
    private val mainViewModel by inject<MainViewModel>()

    @Test
    fun `test load article list with State Pattern`() = runBlockingTest {
        val testObservable = mainViewModel.homeArticleStateLiveData.test()

        mainViewModel.getArticles(null, 0, 10)

        val list = articleRepository.getLocalArticles()?.map {
            ArticleModel(
                id = it.hashCode().toLong(),
                articleId = it.articleId,
                articleImage = it.articleImage,
                articleTitle = it.articleTitle,
                articlePrice = it.articlePrice,
                bookStatus = it.bookStatus,
                sellingLocation = it.sellingLocation,
                chatCount = it.chatCount,
                favoriteCount = it.favoriteCount,
                beforeTime = it.beforeTime
            )
        }

        testObservable.assertValueSequence(
            listOf(
                HomeState.Uninitialized,
                HomeState.Loading,
                list?.let { HomeState.Success(it) } ?: kotlin.run { HomeState.Error }
            )
        )
    }

    @Test
    fun `test load favorite list with State Pattern`() = runBlockingTest {
        val testObservable = mainViewModel.favoriteArticleStateLiveData.test()

        mainViewModel.getFavorites()

        val list = articleRepository.getFavoriteArticles().data?.map {
            FavoriteModel(
                id = it.hashCode().toLong(),
                favoriteId = it.favoriteId,
                articleId = it.articleEntity.articleId,
                articleImage = it.articleEntity.articleImage,
                articleTitle = it.articleEntity.articleTitle,
                articlePrice = it.articleEntity.articlePrice,
                bookStatus = it.articleEntity.bookStatus,
                sellingLocation = it.articleEntity.sellingLocation,
                chatCount = it.articleEntity.chatCount,
                favoriteCount = it.articleEntity.favoriteCount,
                beforeTime = it.articleEntity.beforeTime
            )
        }

        testObservable.assertValueSequence(
            listOf(
                FavoriteState.Uninitialized,
                FavoriteState.Loading,
                list?.let { FavoriteState.Success(list) } ?: kotlin.run { FavoriteState.Error }
            )
        )
    }

}