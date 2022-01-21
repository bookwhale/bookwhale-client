package com.example.bookwhale.screen.main.mypost

import com.example.bookwhale.databinding.FragmentFavoriteBinding
import com.example.bookwhale.databinding.FragmentMypostBinding
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.screen.main.favorite.FavoriteFragment
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MyPostFragment: BaseFragment<MainViewModel, FragmentMypostBinding>() {
    override val viewModel by viewModel<MainViewModel>()

    override fun getViewBinding(): FragmentMypostBinding =
        FragmentMypostBinding.inflate(layoutInflater)

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
//        recyclerView.adapter = adapter
//
//        viewModel.getFavorites()
    }

    override fun observeData() {
        //
    }

    companion object {

        fun newInstance() = MyPostFragment()

        const val TAG = "MyPostFragment"
    }
}