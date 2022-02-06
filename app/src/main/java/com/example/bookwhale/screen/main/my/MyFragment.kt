package com.example.bookwhale.screen.main.my

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.example.bookwhale.MyApp.Companion.appContext
import com.example.bookwhale.R
import com.example.bookwhale.databinding.FragmentMyBinding
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.util.load
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.FlowPreview
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File


class MyFragment : BaseFragment<MyViewModel, FragmentMyBinding>(), HandlePathOzListener.SingleUri {
    override val viewModel by viewModel<MyViewModel>()

    override fun getViewBinding(): FragmentMyBinding = FragmentMyBinding.inflate(layoutInflater)

    private lateinit var handlePathOz: HandlePathOz

    init {

    }

    override fun initViews() {
        initHandlePathOz()
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

            selectSingleImage()
        }
    }


    @FlowPreview
    private fun selectSingleImage() {

        TedImagePicker.with(requireContext())
            .start { uri ->
                handlePathOz.getRealPath(uri)
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

    private fun initHandlePathOz() {
        handlePathOz = HandlePathOz(appContext!!, this)
    }

    override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        var file = File(pathOz.path)
        var requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("profileImage",file.name,requestBody)
        viewModel.updateProfileImage(body)
    }

    companion object {

        fun newInstance() = MyFragment()

        const val TAG = "MyFragment"
    }

}