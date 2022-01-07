package com.example.bookwhale.viewmodel.main

import com.example.bookwhale.data.entity.home.ArticleEntity
import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.viewmodel.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.test.inject

@ExperimentalCoroutinesApi
internal class MainViewModelTest: ViewModelTest() {

    private val articleRepository by inject<ArticleRepository>()
    private val mainViewModel by inject<MainViewModel>()

    @Test
    fun `test load article list`() = runBlockingTest {
        val testObservable = mainViewModel.articleListLiveData.test()

        mainViewModel.getArticles(null,0,10)

        testObservable.assertValueSequence(
            listOf(
                articleRepository.getLocalArticles()?.map {
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
            )
        )

    }

    @Test
    fun `test load favorite list`() = runBlockingTest {
        val testObservable = mainViewModel.favoriteListLiveData.test()

        mainViewModel.getFavorites()

        testObservable.assertValueSequence(
            listOf(
                articleRepository.getFavoriteArticles()?.map {
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
            )
        )

    }

//    @Test
//    fun `test load like article list`() = runBlockingTest {
//        val testObservable = likeListViewModel.likeArticleListLiveData.test()
//
//        likeListViewModel.fetchData()
//
//        testObservable.assertValueSequence(
//            listOf(
//                articleRepository.getLikeArticleList().map {
//                    LikeArticleModel(
//                        id = it.hashCode().toLong(),
//                        likeId = it.likeId,
//                        postResponse = it.postResponse
//
//                    )
//                }
//            )
//        )
//    }



}