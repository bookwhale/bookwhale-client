package com.example.bookwhale.screen.main.my

import android.util.Log
import com.example.bookwhale.databinding.FragmentMyBinding
import com.example.bookwhale.screen.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {
    override val viewModel by viewModel<MyViewModel>()

    override fun getViewBinding(): FragmentMyBinding = FragmentMyBinding.inflate(layoutInflater)

    override fun initViews() {
        Log.e("myFragment","myFragment")
    }

    override fun observeData() {

        viewModel.profileInfo.observe(this) {
            binding.profileTextView.text = it.profileName
        }

    }

    companion object {

        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }
}