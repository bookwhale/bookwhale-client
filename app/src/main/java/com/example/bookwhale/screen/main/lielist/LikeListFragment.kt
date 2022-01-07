package com.example.bookwhale.screen.main.lielist

import com.example.bookwhale.databinding.FragmentLikelistBinding
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.likelist.LikeListListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class LikeListFragment: BaseFragment<LikeListViewModel, FragmentLikelistBinding>() {
    override val viewModel by viewModel<LikeListViewModel>()

    override fun getViewBinding(): FragmentLikelistBinding = FragmentLikelistBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

//    private val adapter by lazy {
//        ModelRecyclerAdapter<LikeArticleModel, LikeListViewModel>(
//            listOf(),
//            viewModel,
//            resourcesProvider,
//            adapterListener = object : LikeListListener {
//                override fun onClickItem(model: LikeArticleModel) {
//                    //
//                }
//            }
//        )
//    }

//    override fun initViews() = with(binding) {
//        recyclerView.adapter = adapter
//    }
//
    override fun observeData() {
//        viewModel.likeArticleListLiveData.observe(this) {
//            adapter.submitList(it)
//        }
    }

    companion object {

        fun newInstance() = LikeListFragment()

        const val TAG = "LikeListFragment"
    }

}