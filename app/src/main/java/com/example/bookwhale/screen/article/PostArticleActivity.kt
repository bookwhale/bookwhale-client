package com.example.bookwhale.screen.article

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.R
import com.example.bookwhale.data.response.article.ArticleDTO
import com.example.bookwhale.databinding.ActivityPostArticleBinding
import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.DEFAULT_IMAGEVIEW_RADIUS
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.article.PostImageListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class PostArticleActivity : BaseActivity<PostArticleViewModel, ActivityPostArticleBinding>() {

    override val viewModel by viewModel<PostArticleViewModel>()

    override fun getViewBinding(): ActivityPostArticleBinding = ActivityPostArticleBinding.inflate(layoutInflater)

    private var naverBookInfo = NaverBookModel()

    private val resourcesProvider by inject<ResourcesProvider>()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val model = it.getParcelableExtra<NaverBookModel>("naverBookModel")
                    model?.let { data ->
                        naverBookInfo = data
                        handleNaverBookApi()
                    }
                } ?: kotlin.run {
                    Toast.makeText(this, getString(R.string.loadError_searchBook), Toast.LENGTH_SHORT).show()
                }
            }
        }

    private lateinit var postInfo: ArticleDTO
    private val files: ArrayList<MultipartBody.Part> = ArrayList()
    private var statusRadioText: String = DEFAULT_STATUS
    private var sellingLocation: String = DEFAULT_LOCATION
    private var imageModelList: ArrayList<DetailImageModel> = ArrayList()
    private var imageUriList: ArrayList<Uri> = ArrayList()

    private val adapter by lazy {
        ModelRecyclerAdapter<DetailImageModel, PostArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : PostImageListener {
                override fun onClickItem(model: DetailImageModel) {
                    Unit
                }

                override fun onDeleteItem(model: DetailImageModel) {
                    removeModel(model)
                }
            }
        )
    }

    private fun removeModel(model: DetailImageModel) {
        var removeIndex = 0
        imageModelList.forEachIndexed { index, data ->
            if (data == model) removeIndex = index
        }
        imageModelList.removeAt(removeIndex)
        imageUriList.removeAt(removeIndex)
        binding.uploadPhotoTextView.text = getString(R.string.currentImageNum, imageUriList.size)
        adapter.notifyItemRemoved(removeIndex)
        adapter.notifyItemRangeChanged(removeIndex, imageUriList.size)
    }

    override fun initViews() {
        binding.recyclerView.adapter = adapter
        enterKeyboard()
        initButton()
    }

    @FlowPreview
    private fun initButton() = with(binding) {
        officialBookNameTextView.setOnClickListener {
            getContent.launch(SearchActivity.newIntent(this@PostArticleActivity))
        }
        officialBookImageLayout.setOnClickListener {
            getContent.launch(SearchActivity.newIntent(this@PostArticleActivity))
        }
        uploadPhotoLayout.setOnClickListener {
            selectMultipleImage()
        }
        uploadPhotoButton.setOnClickListener {
            selectMultipleImage()
        }
        locationTextView.setOnClickListener {
            locationClicked()
        }
        postButton.setOnClickListener {
            postArticle()
        }
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun handleNaverBookApi() = with(binding) {
        naverBookInfo?.let {
            officialBookImageView.isVisible = true
            officialBookImageView.load(it.bookThumbnail, DEFAULT_IMAGEVIEW_RADIUS, CenterCrop())
            officialBookNameTextView.text = it.bookTitle.replace("<b>", "").replace("</b>", "")
            officialWriterTextView.text = getString(R.string.writer, it.bookAuthor.replace("<b>", "").replace("</b>", ""))
            officialPublisherTextView.text = getString(R.string.publisher, it.bookPublisher.replace("<b>", "").replace("</b>", ""))
            officialPriceTextView.text = getString(R.string.price, it.bookListPrice.replace("<b>", "").replace("</b>", ""))
            officialBookNameTextView.setTextColor(ContextCompat.getColor(this@PostArticleActivity, R.color.black))
            officialWriterTextView.setTextColor(ContextCompat.getColor(this@PostArticleActivity, R.color.black))
            officialPublisherTextView.setTextColor(ContextCompat.getColor(this@PostArticleActivity, R.color.black))
            officialPriceTextView.setTextColor(ContextCompat.getColor(this@PostArticleActivity, R.color.black))
        }
    }

    @FlowPreview
    private fun selectMultipleImage() {
        val currentSize = imageUriList.size

        TedImagePicker.with(this)
            .max(MAX_IMAGE_NUM - currentSize, getString(R.string.maxImageNum))
            .mediaType(MediaType.IMAGE)
            .startMultiImage{ uriList ->

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // API 29??????
                    val filePathColumn =
                        arrayOf(
                            MediaStore.MediaColumns.DATA
                        )
                    for (i in uriList.indices) {
                        val cursor: Cursor? = this.contentResolver.query(
                            uriList[i],
                            filePathColumn,
                            null,
                            null,
                            null)

                        cursor?.let {
                            if (cursor.moveToFirst()) {
                                val columnIndex: Int = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                                val absolutePathOfImage: String = cursor.getString(columnIndex)
                                imageUriList.add(Uri.parse(absolutePathOfImage))
                                cursor.close()
                            }
                        }
                        addRecyclerViewList(imageUriList)
                    }
                } else {
                    imageUriList.addAll(uriList)
                    addRecyclerViewList(imageUriList)
                }
            }
    }

    private fun addRecyclerViewList(uriList: List<Uri>) = with(binding) {
        imageModelList.clear()
        imageModelList.addAll(
            uriList.mapIndexed { index, data ->
                DetailImageModel(
                    id = index.toLong(),
                    type = CellType.TEMP_IMAGE_LIST,
                    articleImage = data.path
                )
            }
        )

        adapter.submitList(imageModelList)
        adapter.notifyItemRangeChanged(0, imageModelList.size)
        uploadPhotoTextView.text = getString(R.string.currentImageNum, imageModelList.size)
    }

    private fun enterKeyboard() {
        binding.articlePriceTextView.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                locationClicked()
                true
            } else {
                false
            }
        }
    }

    private fun locationClicked() {

        val items = arrayOf("??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????", "??????")

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.location))
            .setItems(items) { dialog, which ->
                binding.locationTextView.text = items[which]
                sellingLocation = mappingLocation(items[which])
            }
            .show()
    }

    private fun mappingLocation(korean: String): String {
        when (korean) {
            "??????" -> return "SEOUL"
            "??????" -> return "BUSAN"
            "??????" -> return "DAEGU"
            "??????" -> return "INCHEON"
            "??????" -> return "GWANGJU"
            "??????" -> return "DAEJEON"
            "??????" -> return "ULSAN"
            "??????" -> return "SEJONG"
            "??????" -> return "GYEONGGI"
            "??????" -> return "GANGWON"
            "??????" -> return "CHUNGBUK"
            "??????" -> return "CHUNGNAM"
            "??????" -> return "JEONBUK"
            "??????" -> return "JEONNAM"
            "??????" -> return "GYEONGBUK"
            "??????" -> return "GYEONGNAM"
            "??????" -> return "JEJU"
            else -> return "SEOUL"
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            when (view.getId()) {
                R.id.radio_best ->
                    if (checked) {
                        statusRadioText = "BEST"
                    }
                R.id.radio_high ->
                    if (checked) {
                        statusRadioText = "UPPER"
                    }
                R.id.radio_mid ->
                    if (checked) {
                        statusRadioText = "MIDDLE"
                    }
                R.id.radio_low ->
                    if (checked) {
                        statusRadioText = "LOWER"
                    }
            }
        }
    }

    private fun postArticle() = with(binding) {

        if (checkInputInfo()) {

            uploadPhoto()
            uploadDesc()

            lifecycleScope.launch {
                viewModel.uploadArticle(files, postInfo).join()
            }
        }
    }

    private fun checkInputInfo(): Boolean = with(binding) {
        if (naverBookInfo.bookTitle.isNotEmpty()) {
            when {
                imageModelList.isEmpty() -> {
                    Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_image), Toast.LENGTH_SHORT).show()
                    return false
                }
                articleNameTextView.text.isEmpty() -> {
                    Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_title), Toast.LENGTH_SHORT).show()
                    return false
                }
                articlePriceTextView.text.isEmpty() -> {
                    Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_price), Toast.LENGTH_SHORT).show()
                    return false
                }
                locationTextView.text.isEmpty() -> {
                    Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_location), Toast.LENGTH_SHORT).show()
                    return false
                }
                statusRadioText.isEmpty() -> {
                    Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_status), Toast.LENGTH_SHORT).show()
                    return false
                }
                descriptionTextView.text.isEmpty() -> {
                    Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_description), Toast.LENGTH_SHORT).show()
                    return false
                }
                else -> return true
            }
        } else {
            Toast.makeText(this@PostArticleActivity, getString(R.string.searchBookName), Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun uploadPhoto() {
        for (element in imageModelList) {
            val file = File(element.articleImage!!)
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("images", file.name, requestBody)
            files.add(body)
        }
    }
    private fun uploadDesc() = with(binding) {
        postInfo = ArticleDTO(
            bookRequest = ArticleDTO.BookRequest(
                bookIsbn = naverBookInfo.bookIsbn.replace("<b>", "").replace("</b>", ""),
                bookTitle = naverBookInfo.bookTitle.replace("<b>", "").replace("</b>", ""),
                bookAuthor = naverBookInfo.bookAuthor.replace("<b>", "").replace("</b>", ""),
                bookPublisher = naverBookInfo.bookPublisher.replace("<b>", "").replace("</b>", ""),
                bookThumbnail = naverBookInfo.bookThumbnail,
                bookListPrice = naverBookInfo.bookListPrice.replace("<b>", "").replace("</b>", ""),
                bookPubDate = naverBookInfo.bookPubDate ?: kotlin.run { "null" },
                bookSummary = naverBookInfo.bookSummary.replace("<b>", "").replace("</b>", "")
            ),
            title = articleNameTextView.text.toString(),
            price = articlePriceTextView.text.toString(),
            description = descriptionTextView.text.toString(),
            bookStatus = statusRadioText,
            sellingLocation = sellingLocation,
        )
    }

    override fun observeData() {
        viewModel.postArticleStateLiveData.observe(this) {
            when (it) {
                is PostArticleState.Uninitialized -> Unit
                is PostArticleState.Loading -> handleLoading()
                is PostArticleState.Success -> handleSuccess()
                is PostArticleState.Error -> handleError(it)
            }
        }
    }

    private fun handleLoading() = with(binding) {
        progressBar.isVisible = true
    }

    private fun handleSuccess() = with(binding) {
        progressBar.isGone = true
        finish()
    }

    private fun handleError(state: PostArticleState.Error) = with(binding) {
        progressBar.isGone = true
        when (state.code!!) {
            "T_004" -> handleT004() // AccessToken ?????? ??????
            else -> handleUnexpected(state.code)
        }
    }

    private fun handleUnexpected(code: String) {
        Toast.makeText(this, getString(R.string.error_unKnown, code), Toast.LENGTH_SHORT).show()
    }

    private fun handleT004() {
        lifecycleScope.launch {
            viewModel.getNewTokens().join()
            viewModel.uploadArticle(files, postInfo)
        }
    }

    companion object {

        fun newIntent(context: Context, bookInfo: NaverBookModel?) =
            Intent(context, PostArticleActivity::class.java).apply {
                putExtra(NAVER_BOOK_INFO, bookInfo)
            }

        const val NAVER_BOOK_INFO = "NaverBookInfo"
        const val MAX_IMAGE_NUM = 5

        const val DEFAULT_STATUS = "UPPER"
        const val DEFAULT_LOCATION = "SEOUL"

        const val NAVER_BOOK_REQUEST_CODE = 1001
    }
}
