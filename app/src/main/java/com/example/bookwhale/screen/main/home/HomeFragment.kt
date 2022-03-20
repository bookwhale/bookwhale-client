package com.example.bookwhale.screen.main.home

import android.util.Log
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.bookwhale.databinding.FragmentHomeBinding
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.article.DetailArticleActivity
import com.example.bookwhale.screen.article.PostArticleActivity
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.PagingAdapter
import com.example.bookwhale.widget.listener.main.home.ArticleListListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class HomeFragment: BaseFragment<MainViewModel, FragmentHomeBinding>() {
    override val viewModel by activityViewModels<MainViewModel>()

    override fun getViewBinding(): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        PagingAdapter(
            resourcesProvider = resourcesProvider,
            adapterListener = object : ArticleListListener {
                override fun onClickItem(model: ArticleModel) {
                    startActivity(DetailArticleActivity.newIntent(requireContext(), model.articleId.toString()))
                }
            }
        )
    }

    override fun initViews(): Unit = with(binding) {

        initButton()

        recyclerView.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                noArticleTextView.isVisible = true
                binding.progressBar.isGone = true
            } else {
                noArticleTextView.isGone = true
                binding.progressBar.isGone = true
            }
        }

        lifecycleScope.launch {
            getArticles(null)
        }

        swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
            swipeRefreshLayout.isRefreshing = false
        }

    }

     suspend fun getArticles(search: String?) {
        lifecycleScope.launch {
            viewModel.getArticlesPaging(search).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.refresh()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun initButton() = with(binding) {
        postArticleButton.setOnClickListener {
            startActivity(PostArticleActivity.newIntent(requireContext(),null))
        }
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
        //binding.progressBar.isGone = true

    }

    private fun handleError(state: HomeState.Error) {
        binding.progressBar.isGone = true
        when(state.code!!) {
            "T_004" -> handleT004() // AccessToken 만료 코드
        }
    }

    private fun handleT004() {
        lifecycleScope.launch {
            viewModel.getNewTokens().join()
            viewModel.getArticlesPaging(null).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    companion object {

        fun newInstance() = HomeFragment()

        const val TAG = "HomeFragment"
    }
}