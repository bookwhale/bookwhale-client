package com.example.bookwhale.screen.main.my

import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.bookwhale.R
import com.example.bookwhale.databinding.FragmentMyBinding
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.util.load
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>() {
    override val viewModel by viewModel<MyViewModel>()

    override fun getViewBinding(): FragmentMyBinding = FragmentMyBinding.inflate(layoutInflater)

    override fun initViews() {
        handleButton()
    }

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

    private fun handleButton() = with(binding) {
        profileTextView.setOnClickListener {
            updateNameGroup.isVisible = true
        }

        confirmButton.setOnClickListener {
            val name = updateNameEditText.text

            if(!name.isNullOrEmpty()) {
                viewModel.updateNickName(name.toString())
                updateNameEditText.text.clear()
                updateNameGroup.isGone = true
            } else {
                errorTextView.text = getString(R.string.error_noNickName)
            }
        }

        cancelButton.setOnClickListener {
            updateNameGroup.isGone = true
        }

        profileImageView.setOnClickListener {
            updateProfileImage()
        }
    }

    private fun updateProfileImage() {
        var file = File("https://i1.daumcdn.net/thumb/C264x200/?fname=https://blog.kakaocdn.net/dn/bcHq0P/btro14dTsMA/b5Lcjz1jT6KMJCxhfrKzHK/img.jpg")
        var requestBody : RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(),file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("profileImage",file.name,requestBody)
        viewModel.updateProfileImage(body)
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