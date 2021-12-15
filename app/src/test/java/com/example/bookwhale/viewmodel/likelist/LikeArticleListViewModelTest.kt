package com.example.bookwhale.viewmodel.likelist

import com.example.bookwhale.data.repository.main.ArticleRepository
import com.example.bookwhale.model.main.likelist.LikeArticleModel
import com.example.bookwhale.screen.main.lielist.LikeListViewModel
import com.example.bookwhale.viewmodel.ViewModelTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.koin.test.inject

@ExperimentalCoroutinesApi
internal class LikeArticleListViewModelTest: ViewModelTest() {

    private val articleRepository by inject<ArticleRepository>()
    private val likeListViewModel by inject<LikeListViewModel>()

    @Test
    fun `test load like article list`() = runBlockingTest {
        val testObservable = likeListViewModel.likeArticleListLiveData.test()

        likeListViewModel.fetchData()

        testObservable.assertValueSequence(
            listOf(
                articleRepository.getLikeArticleList().map {
                    LikeArticleModel(
                        id = it.hashCode().toLong(),
                        likeId = it.likeId,
                        postResponse = it.postResponse

                    )
                }
            )
        )
    }



}