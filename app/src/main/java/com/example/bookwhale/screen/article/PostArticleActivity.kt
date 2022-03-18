package com.example.bookwhale.screen.article

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
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
            if(result.resultCode == Activity.RESULT_OK) {
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
    private var statusRadioText : String = DEFAULT_STATUS
    private var sellingLocation : String = DEFAULT_LOCATION
    private var imageModelList: ArrayList<DetailImageModel> = ArrayList()
    private var imageUriList: ArrayList<Uri> = ArrayList()

    private val adapter by lazy {
        ModelRecyclerAdapter<DetailImageModel, PostArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : PostImageListener {
                override fun onClickItem(model: DetailImageModel) {
                    //
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

    @SuppressLint("SetTextI18n")
    private fun handleNaverBookApi() = with(binding) {
        naverBookInfo?.let {
            officialBookImageView.isVisible = true
            officialBookImageView.load(it.bookThumbnail,4f, CenterCrop())
            officialBookNameTextView.text = it.bookTitle.replace("<b>","").replace("</b>","")
            officialWriterTextView.text = "글 ${it.bookAuthor.replace("<b>","").replace("</b>","")}"
            officialPublisherTextView.text = "출판 ${it.bookPublisher.replace("<b>","").replace("</b>","")}"
            officialPriceTextView.text = "${it.bookListPrice.replace("<b>","").replace("</b>","")}원"
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

            }
    }

    private fun addRecyclerViewList(uriList : List<Uri>) = with(binding) {
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

    private fun locationClicked() {

        val items = arrayOf("서울","부산","대구","인천","광주","대전","울산","세종","경기","강원","충북","충남","전북","전남","경북","경남","제주")

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.location))
            .setItems(items) { dialog, which ->
                binding.locationTextView.text = items[which]
                sellingLocation = mappingLocation(items[which])
            }
            .show()
    }

    private fun mappingLocation(korean: String):String {
        when(korean) {
            "서울" -> return "SEOUL"
            "부산" -> return "BUSAN"
            "대구" -> return "DAEGU"
            "인천" -> return "INCHEON"
            "광주" -> return "GWANGJU"
            "대전" -> return "DAEJEON"
            "울산" -> return "ULSAN"
            "세종" -> return "SEJONG"
            "경기" -> return "GYEONGGI"
            "강원" -> return "GANGWON"
            "충북" -> return "CHUNGBUK"
            "충남" -> return "CHUNGNAM"
            "전북" -> return "JEONBUK"
            "전남" -> return "JEONNAM"
            "경북" -> return "GYEONGBUK"
            "경남" -> return "GYEONGNAM"
            "제주" -> return "JEJU"
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
        if(naverBookInfo.bookTitle.isNotEmpty()) {
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
            val body : MultipartBody.Part = MultipartBody.Part.createFormData("images",file.name,requestBody)
            files.add(body)
        }
    }
    private fun uploadDesc() = with(binding) {
        postInfo = ArticleDTO(
            bookRequest = ArticleDTO.BookRequest(
                bookIsbn = naverBookInfo.bookIsbn.replace("<b>","").replace("</b>",""),
                bookTitle = naverBookInfo.bookTitle.replace("<b>","").replace("</b>",""),
                bookAuthor = naverBookInfo.bookAuthor.replace("<b>","").replace("</b>",""),
                bookPublisher = naverBookInfo.bookPublisher.replace("<b>","").replace("</b>",""),
                bookThumbnail = naverBookInfo.bookThumbnail,
                bookListPrice = naverBookInfo.bookListPrice.replace("<b>","").replace("</b>",""),
                bookPubDate = naverBookInfo.bookPubDate ?: kotlin.run { "null" },
                bookSummary = naverBookInfo.bookSummary.replace("<b>","").replace("</b>","")
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
            when(it) {
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
        when(state.code!!) {
            "T_004" -> handleT004() // AccessToken 만료 코드
            else -> handleUnexpected(state.code)
        }
    }

    private fun handleUnexpected(code: String) {
        Toast.makeText(this, getString(R.string.error_unKnown, code), Toast.LENGTH_SHORT).show()
    }

    private fun handleT004() {
        lifecycleScope.launch {
            viewModel.getNewTokens().join()
            viewModel.uploadArticle(files,postInfo)
        }
    }

    companion object {

        fun newIntent(context: Context, bookInfo: NaverBookModel?) = Intent(context, PostArticleActivity::class.java).apply {
            putExtra(NAVER_BOOK_INFO, bookInfo)
        }

        const val NAVER_BOOK_INFO = "NaverBookInfo"
        const val MAX_IMAGE_NUM = 5

        const val DEFAULT_STATUS = "UPPER"
        const val DEFAULT_LOCATION = "SEOUL"

        const val NAVER_BOOK_REQUEST_CODE = 1001
    }

}