package com.example.bookwhale.screen.main.mypost

import android.util.Log
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.databinding.FragmentFavoriteBinding
import com.example.bookwhale.databinding.FragmentMypostBinding
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.article.DetailArticleActivity
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.screen.main.favorite.FavoriteFragment
import com.example.bookwhale.screen.main.favorite.FavoriteState
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
import com.example.bookwhale.widget.listener.main.home.ArticleListListener
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MyPostFragment: BaseFragment<MainViewModel, FragmentMypostBinding>() {
    override val viewModel by activityViewModels<MainViewModel>()

    override fun getViewBinding(): FragmentMypostBinding =
        FragmentMypostBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<ArticleModel, MainViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : ArticleListListener {
                override fun onClickItem(model: ArticleModel) {
                    startActivity(DetailArticleActivity.newIntent(requireContext(), model.articleId.toString()))
                }
            }
        )
    }

    override fun initViews(): Unit = with(binding) {
        recyclerView.adapter = adapter

        viewModel.getMyArticles()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyArticles()
    }

    override fun observeData() {
        viewModel.myArticleStateLiveData.observe(this) {
            when(it) {
                is MyPostState.Uninitialized -> Unit
                is MyPostState.Loading -> handleLoading()
                is MyPostState.Success -> handleSuccess(it)
                is MyPostState.Error -> handleError(it)
            }
        }
    }

    private fun handleLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleError(state: MyPostState.Error) {
        binding.progressBar.isGone = true
        when(state.code!!) {
            "T_004" -> handleT004() // AccessToken 만료 코드
        }
    }

    private fun handleT004() {
        lifecycleScope.launch {
            viewModel.getNewTokens().join()
            viewModel.getFavorites().join()
        }
    }

    private fun handleSuccess(state: MyPostState.Success) {
        binding.progressBar.isGone = true
        adapter.submitList(state.myArticles)
        if(state.myArticles.isEmpty()) {
            binding.noArticleTextView.isVisible = true
        } else {
            binding.noArticleTextView.isGone = true
        }
    }

    companion object {

        fun newInstance() = MyPostFragment()

        const val TAG = "MyPostFragment"
    }
}