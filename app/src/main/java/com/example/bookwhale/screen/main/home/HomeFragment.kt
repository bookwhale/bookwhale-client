package com.example.bookwhale.screen.main.home

import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.R
import com.example.bookwhale.databinding.FragmentHomeBinding
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.util.PagingAdapter
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.home.ArticleListListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class HomeFragment: BaseFragment<MainViewModel, FragmentHomeBinding>() {
    override val viewModel by activityViewModels<MainViewModel>()

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter = PagingAdapter()

//    private val adapter by lazy {
//        ModelRecyclerAdapter<ArticleModel, MainViewModel>(
//            listOf(),
//            viewModel,
//            resourcesProvider,
//            adapterListener = object : ArticleListListener {
//                override fun onClickItem(model: ArticleModel) {
//                }
//
//                override fun onClickHeart(model: ArticleModel) {
//                    // 좋아요 버튼 클릭
//                    clickFavoriteButton(model.articleId)
//                    notifyData()
//                }
//            }
//        )
//    }

    override fun initViews(): Unit = with(binding) {

        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.getArticlesPaging(null).collectLatest {
                adapter.submitData(it)
            }
        }
    }


    /**
     * 매우 불완전한 코드이므로 수정할 예정.
     *
     * */
    private fun clickFavoriteButton(articleId: Int) {

        viewModel.favoriteList?.forEach {
            if(it.articleId == articleId) {
                viewModel.deleteFavoriteInHome(it.favoriteId)
                return
            } else {
                viewModel.addFavoriteInHome(articleId)
            }
        }?.run {
            viewModel.addFavoriteInHome(articleId)
        }
    }

    private fun notifyData() {
        //adapter.submitList(viewModel.articleList as List<ArticleModel>)
    }

    override fun observeData() {
        viewModel.homeArticleStateLiveData.observe(this) {
            when(it) {
                is HomeState.Loading -> handleLoading()
                is HomeState.Success -> handleSuccess()
                is HomeState.Error -> handleError(it)
                else -> Unit
            }
        }
    }

    private fun handleLoading() {
        Log.e(TAG,"handleLoading")
        binding.progressBar.isVisible = true
    }

    private fun handleSuccess() {
        Log.e(TAG,"handleSuccess")
        binding.progressBar.isGone = true
        //adapter.submitList(state.articles)
        binding.noArticleTextView.isVisible = adapter.itemCount != 0
    }

    private fun handleError(state: HomeState.Error) {
        Log.e(TAG,"handleError")
        binding.progressBar.isGone = true
        Toast.makeText(requireContext(), R.string.error_noArticles, Toast.LENGTH_SHORT).show()
    }

    companion object {

        fun newInstance() = HomeFragment()

        const val TAG = "HomeFragment"
        const val PAGE = 0
        const val SIZE = 10
    }
}