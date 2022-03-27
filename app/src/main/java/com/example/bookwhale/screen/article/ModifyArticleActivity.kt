package com.example.bookwhale.screen.article

import com.example.bookwhale.databinding.ActivityModifyArticleBinding
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.MyApp
import com.example.bookwhale.R
import com.example.bookwhale.data.response.article.ModifyArticleDTO
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


class ModifyArticleActivity : BaseActivity<ModifyArticleViewModel, ActivityModifyArticleBinding>() {

    override val viewModel by viewModel<ModifyArticleViewModel>()

    override fun getViewBinding(): ActivityModifyArticleBinding = ActivityModifyArticleBinding.inflate(layoutInflater)

    private var naverBookInfo = NaverBookModel()

    private val resourcesProvider by inject<ResourcesProvider>()

    private val articleId by lazy { intent.getStringExtra(ARTICLE_ID)!! }

    private lateinit var postInfo: ModifyArticleDTO
    private val files: ArrayList<MultipartBody.Part> = ArrayList()
    private var statusRadioText : String = DEFAULT_STATUS
    private var sellingLocation : String = DEFAULT_LOCATION
    private var imageModelList: ArrayList<DetailImageModel> = ArrayList()
    private var imageUriList: ArrayList<String> = ArrayList()
    private var deleteImageList: ArrayList<String> = ArrayList()

    private val adapter by lazy {
        ModelRecyclerAdapter<DetailImageModel, ModifyArticleViewModel>(
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
        if(imageModelList.elementAt(removeIndex).articleImage?.contains("https")==true)
        {
            deleteImageList.add(imageModelList.elementAt(removeIndex).articleImage.toString())
        }
        imageModelList.removeAt(removeIndex)
        imageUriList.removeAt(removeIndex)
        binding.uploadPhotoTextView.text = getString(R.string.currentImageNum, imageUriList.size)
        adapter.notifyItemRemoved(removeIndex)
        adapter.notifyItemRangeChanged(removeIndex, imageUriList.size)
    }

    override fun initViews() {

        binding.recyclerView.adapter = adapter
        viewModel.loadArticle(articleId.toInt())
        initButton()
    }

    @FlowPreview
    private fun initButton() = with(binding) {
        /*officialBookNameTextView.setOnClickListener {//naver 책 고치는 부분
            getContent.launch(com.example.bookwhale.screen.article.SearchActivity.newIntent(this@ModifyArticleActivity))
        }
        officialBookImageLayout.setOnClickListener {
            getContent.launch(com.example.bookwhale.screen.article.SearchActivity.newIntent(this@ModifyArticleActivity))
        }*/
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

    @FlowPreview
    private fun selectMultipleImage() {
        val currentSize = imageUriList.size

        TedImagePicker.with(this)
            .max(PostArticleActivity.MAX_IMAGE_NUM - currentSize, getString(R.string.maxImageNum))
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

                            imageUriList.add(absolutePathOfImage)

                            cursor.close()
                        }
                    }
                    addRecyclerViewList(imageUriList)
                }

            }
    }

    private fun addRecyclerViewList(uriList : List<String>) = with(binding) {
        imageModelList.clear()
        imageModelList.addAll(
            uriList.mapIndexed { index, data ->
                DetailImageModel(
                    id = index.toLong(),
                    type = CellType.TEMP_IMAGE_LIST,
                    articleImage = data
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
                viewModel.uploadArticle(articleId.toInt(),files, postInfo).join()
            }
        }
    }

    private fun checkInputInfo(): Boolean = with(binding) {
        when {
            imageModelList.isEmpty() -> {
                android.widget.Toast.makeText(this@ModifyArticleActivity, getString(com.example.bookwhale.R.string.inputError_image), android.widget.Toast.LENGTH_SHORT).show()
                return false
            }
            articleNameTextView.text.isEmpty() -> {
                android.widget.Toast.makeText(this@ModifyArticleActivity, getString(com.example.bookwhale.R.string.inputError_title), android.widget.Toast.LENGTH_SHORT).show()
                return false
            }
            articlePriceTextView.text.isEmpty() -> {
                android.widget.Toast.makeText(this@ModifyArticleActivity, getString(com.example.bookwhale.R.string.inputError_price), android.widget.Toast.LENGTH_SHORT).show()
                return false
            }
            locationTextView.text.isEmpty() -> {
                android.widget.Toast.makeText(this@ModifyArticleActivity, getString(com.example.bookwhale.R.string.inputError_location), android.widget.Toast.LENGTH_SHORT).show()
                return false
            }
            statusRadioText.isEmpty() -> {
                android.widget.Toast.makeText(this@ModifyArticleActivity, getString(com.example.bookwhale.R.string.inputError_status), android.widget.Toast.LENGTH_SHORT).show()
                return false
            }
            descriptionTextView.text.isEmpty() -> {
                android.widget.Toast.makeText(this@ModifyArticleActivity, getString(com.example.bookwhale.R.string.inputError_description), android.widget.Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }

    private fun uploadPhoto() {//delte가 없으면 돌아가면 안되나?
        for (element in imageModelList) {
            if(element.articleImage?.contains("https")==false)
            {
                val file = File(element.articleImage!!)
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body: MultipartBody.Part =
                    MultipartBody.Part.createFormData("images", file.name, requestBody)
                files.add(body)
            }
        }
    }
    private fun uploadDesc() = with(binding) {
        postInfo = ModifyArticleDTO(
            title = articleNameTextView.text.toString(),
            price = articlePriceTextView.text.toString(),
            description = descriptionTextView.text.toString(),
            bookStatus = statusRadioText,
            sellingLocation = sellingLocation,
            deleteImgUrls = deleteImageList
        )
        Log.e("postInfo",postInfo.toString())
    }

    override fun observeData() {
        viewModel.modifyArticleStateLiveData.observe(this) {
            when(it) {
                is ModifyArticleState.Uninitialized -> Unit
                is ModifyArticleState.Loading -> handleLoading()
                is ModifyArticleState.LoadSuccess-> handleLoadSuccess(it)
                is ModifyArticleState.ModifySuccess-> handleSuccess()
                is ModifyArticleState.Error -> handleError(it)
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

    @SuppressLint("SetTextI18n")
    private fun handleLoadSuccess(state: ModifyArticleState.LoadSuccess) =with(binding){
        binding.progressBar.isGone = true

        articleNameTextView.setText(state.article.title)
        articlePriceTextView.setText("${state.article.price}")
        radioGroup.check(changeStatus(state.article.bookStatus))
        locationTextView.text = state.article.sellingLocation
        sellingLocation=mappingLocation(state.article.sellingLocation)
        descriptionTextView.setText(state.article.description)
        officialBookNameTextView.text = state.article.bookResponse.bookTitle
        officialBookImageView.isVisible = true
        officialBookImageView.load(state.article.bookResponse.bookThumbnail,4f,CenterCrop())
        officialPriceTextView.text = "${state.article.bookResponse.bookListPrice}원"
        officialWriterTextView.text = "글 ${state.article.bookResponse.bookAuthor}"
        officialPublisherTextView.text = "출판 ${state.article.bookResponse.bookPublisher}"
        officialBookNameTextView.setTextColor(ContextCompat.getColor(this@ModifyArticleActivity, R.color.black))
        officialWriterTextView.setTextColor(ContextCompat.getColor(this@ModifyArticleActivity, R.color.black))
        officialPublisherTextView.setTextColor(ContextCompat.getColor(this@ModifyArticleActivity, R.color.black))
        officialPriceTextView.setTextColor(ContextCompat.getColor(this@ModifyArticleActivity, R.color.black))

        for(i in 0 until state.article.images.size)
            imageUriList.add(state.article.images[i].articleImage.toString())

        addRecyclerViewList(imageUriList)

    }

    private fun handleError(state: ModifyArticleState.Error) = with(binding) {
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
            viewModel.uploadArticle(articleId.toInt(),files,postInfo)
        }
    }

    private fun changeStatus(now : String) : Int {
        when (now) {
            "최상" -> {
                statusRadioText="BEST"
                return R.id.radio_best
            }
            "상" -> {
                statusRadioText = "UPPER"
                return R.id.radio_high
            }
            "중" -> {
                statusRadioText = "MIDDLE"
                return R.id.radio_mid
            }
            "하" -> {
                return R.id.radio_low
                statusRadioText = "LOWER"
            }
        }
        return R.id.radio_high
    }

    companion object {

        fun newIntent(context: Context, articleId: String) = Intent(context, ModifyArticleActivity::class.java).apply {
            putExtra(ARTICLE_ID, articleId)
        }
        const val ARTICLE_ID = "0"
        const val NAVER_BOOK_INFO = "NaverBookInfo"
        const val MAX_IMAGE_NUM = 5

        const val DEFAULT_STATUS = "UPPER"
        const val DEFAULT_LOCATION = "SEOUL"

        const val NAVER_BOOK_REQUEST_CODE = 1001
    }

}

