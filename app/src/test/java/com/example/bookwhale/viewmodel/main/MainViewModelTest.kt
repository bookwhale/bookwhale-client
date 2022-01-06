package com.example.bookwhale.viewmodel.main

import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.viewmodel.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.test.inject

@ExperimentalCoroutinesApi
internal class MainViewModelTest: ViewModelTest() {

    private val articleRepository by inject<ArticleRepository>()
    private val mainViewModel by inject<MainViewModel>()

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