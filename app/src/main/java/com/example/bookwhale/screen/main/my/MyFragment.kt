package com.example.bookwhale.screen.main.my

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.R
import com.example.bookwhale.databinding.FragmentMyBinding
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.screen.main.MainActivity
import com.example.bookwhale.screen.splash.SplashActivity
import com.example.bookwhale.util.load
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.bind
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
                is MyState.LogOutSuccess -> handleLogOut()
                is MyState.WithDrawSuccess -> handleWithDraw()
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
            confirmName()
        }

        updateNameEditText.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                confirmName()
                true
            } else {
                false
            }
        }

        cancelButton.setOnClickListener {
            updateNameGroup.isGone = true
        }

        profileImageView.setOnClickListener {

            selectSingleImage()
        }

        logoutTextView.setOnClickListener {
            viewModel.logOut()
        }

        withdrawTextView.setOnClickListener {
            viewModel.withDraw()
        }
    }

    private fun confirmName() = with(binding){
        val name = updateNameEditText.text

        if(!name.isNullOrEmpty()) {
            viewModel.updateNickName(name.toString())
            updateNameEditText.text.clear()
            updateNameGroup.isGone = true
            keyboardHandle(true)
        } else {
            errorTextView.text = getString(R.string.error_noNickName)
        }
    }

    private fun keyboardHandle(handle: Boolean) {
        val imm =MainActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (handle) {//내리기
            imm.hideSoftInputFromWindow(binding.updateNameEditText.windowToken, 0)
        } else {//올리기
            imm.showSoftInput(binding.updateNameEditText, 0)
        }
    }

    private fun selectSingleImage() {
        TedImagePicker.with(requireContext())
            .mediaType(MediaType.IMAGE)
            .start{ uri: Uri ->
                val filePathColumn =
                    arrayOf(
                        MediaStore.MediaColumns.DATA
                    )
                val cursor: Cursor? = requireContext().contentResolver.query(
                    uri,
                    filePathColumn,
                    null,
                    null,
                    null)

                cursor?.let {
                    if (cursor.moveToFirst()) {
                        val columnIndex: Int = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                        val absolutePathOfImage: String = cursor.getString(columnIndex)
                        Uri.parse(absolutePathOfImage)

                        uploadFile(File(absolutePathOfImage))

                        cursor.close()
                    }
                }
            }
    }

    private fun uploadFile(file: File) {
        var requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("profileImage",file.name,requestBody)
        viewModel.updateProfileImage(body)
    }



    private fun handleLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleWithDraw() {
        binding.progressBar.isGone = true
        viewModel.deleteSavedToken()
        val intent = SplashActivity.newIntent(requireContext())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        Toast.makeText(requireContext(), getString(R.string.success_withdraw), Toast.LENGTH_SHORT).show()
    }

    private fun handleLogOut() {
        binding.progressBar.isGone = true
        viewModel.deleteSavedToken()
        val intent = SplashActivity.newIntent(requireContext())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        Toast.makeText(requireContext(), getString(R.string.success_logout), Toast.LENGTH_SHORT).show()
    }


    private fun handleSuccess(state: MyState.Success) {
        binding.progressBar.isGone = true
        binding.profileTextView.text = state.myInfo.nickName
        state.myInfo.profileImage?.let { url -> binding.profileImageView.load(url, 16f, CenterCrop()) }
    }

    private fun handleError(state: MyState.Error) {
        binding.progressBar.isGone = true
        Toast.makeText(requireContext(), getString(R.string.error_unKnown, state.code), Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance() = MyFragment()
        const val TAG = "MyFragment"
    }

}