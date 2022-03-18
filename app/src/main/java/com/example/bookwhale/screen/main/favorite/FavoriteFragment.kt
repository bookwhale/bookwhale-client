package com.example.bookwhale.screen.main.favorite

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.databinding.FragmentFavoriteBinding
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.article.DetailArticleActivity
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FavoriteFragment: BaseFragment<MainViewModel, FragmentFavoriteBinding>() {
    override val viewModel by activityViewModels<MainViewModel>()

    override fun getViewBinding(): FragmentFavoriteBinding = FragmentFavoriteBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<FavoriteModel, MainViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : FavoriteListener {
                override fun onClickItem(model: FavoriteModel) {
                    startActivity(DetailArticleActivity.newIntent(requireContext(), model.articleId.toString()))
                }

                override fun onClickHeart(model: FavoriteModel) {
                    //clickFavoriteButton(model)
                }
            }
        )
    }

    override fun initViews(): Unit = with(binding) {
        recyclerView.adapter = adapter

        viewModel.getFavorites()
    }
//
    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }

    override fun observeData() {
        viewModel.favoriteArticleStateLiveData.observe(this) {
            when(it) {
                is FavoriteState.Loading -> handleLoading()
                is FavoriteState.Success -> handleSuccess(it)
                is FavoriteState.Error -> handleError(it)
                is FavoriteState.Uninitialized -> Unit
            }
        }
    }

    private fun handleLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleError(state: FavoriteState.Error) {
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

    private fun handleSuccess(state: FavoriteState.Success) {
        binding.progressBar.isGone = true
        adapter.submitList(state.favorites.reversed())
        if(state.favorites.isEmpty()) {
            binding.noArticleTextView.isVisible = true
        } else {
            binding.noArticleTextView.isGone = true
        }
    }


    companion object {

        fun newInstance() = FavoriteFragment()

        const val TAG = "FavoriteFragment"
    }

}