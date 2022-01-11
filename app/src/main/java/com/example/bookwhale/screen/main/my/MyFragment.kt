package com.example.bookwhale.screen.main.my

import android.util.Log
import android.widget.Toast
import com.example.bookwhale.R
import com.example.bookwhale.databinding.FragmentMyBinding
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.util.load
import org.koin.android.viewmodel.ext.android.viewModel

class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {
    override val viewModel by viewModel<MyViewModel>()

    override fun getViewBinding(): FragmentMyBinding = FragmentMyBinding.inflate(layoutInflater)

    override fun observeData() {

        viewModel.profileStateLiveData.observe(this) {
            when(it) {
                is MyState.Uninitialized -> Unit
                is MyState.Loading -> handleLoading()
                is MyState.Success -> handleSuccess(it)
                is MyState.Error -> handleError(it)
            }
        }

    }

    private fun handleLoading() {}

    private fun handleSuccess(it: MyState.Success) {
        binding.profileTextView.text = it.myInfo.nickName
        it.myInfo.profileImage?.let { url -> binding.profileImageView.load(url) }
    }

    private fun handleError(it: MyState.Error) {
        Toast.makeText(requireContext(), getString(R.string.error_unKnown, it.code), Toast.LENGTH_SHORT).show()
    }


    companion object {

        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }
}