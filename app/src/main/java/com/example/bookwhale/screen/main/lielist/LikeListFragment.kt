package com.example.bookwhale.screen.main.lielist

import com.example.bookwhale.databinding.FragmentLikelistBinding
import com.example.bookwhale.screen.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class LikeListFragment: BaseFragment<LikeListViewModel, FragmentLikelistBinding>() {
    override val viewModel by viewModel<LikeListViewModel>()

    override fun getViewBinding(): FragmentLikelistBinding = FragmentLikelistBinding.inflate(layoutInflater)

    override fun initViews() = with(binding) {

    }

    override fun observeData() {
    }

    companion object {

        fun newInstance() = LikeListFragment()

        const val TAG = "LikeListFragment"
    }
}