package com.example.bookwhale.screen.main.favorite

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.bookwhale.databinding.FragmentFavoriteBinding
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class FavoriteFragment: BaseFragment<MainViewModel, FragmentFavoriteBinding>() {
    override val viewModel by viewModel<MainViewModel>()

    override fun getViewBinding(): FragmentFavoriteBinding = FragmentFavoriteBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<FavoriteModel, MainViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : FavoriteListener {
                override fun onClickItem(model: FavoriteModel) {
                    //
                }
            }
        )
    }

    override fun initViews(): Unit = with(binding) {
        recyclerView.adapter = adapter

        viewModel.getFavorites()
    }
//
    override fun observeData() {
        viewModel.favoriteArticleStateLiveData.observe(this) {
            when(it) {
                is FavoriteState.Loading -> handleLoading()
                is FavoriteState.Success -> handleSuccess(it)
                is FavoriteState.Error -> handleError()
                is FavoriteState.Uninitialized -> Unit
            }
        }
    }
    private fun handleLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleError() {
        binding.progressBar.isGone = true
    }

    private fun handleSuccess(state: FavoriteState.Success) {
        adapter.submitList(state.favorites)
        binding.progressBar.isGone = true
    }


    companion object {

        fun newInstance() = FavoriteFragment()

        const val TAG = "FavoriteFragment"
    }

}