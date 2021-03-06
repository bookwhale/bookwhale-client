package com.example.bookwhale.screen.article

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
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.R
import com.example.bookwhale.data.response.article.ModifyArticleDTO
import com.example.bookwhale.databinding.ActivityModifyArticleBinding
import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.DEFAULT_IMAGEVIEW_RADIUS
import com.example.bookwhale.util.EventBus
import com.example.bookwhale.util.Events
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.article.PostImageListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class ModifyArticleActivity : BaseActivity<ModifyArticleViewModel, ActivityModifyArticleBinding>() {

    override val viewModel by viewModel<ModifyArticleViewModel>()

    override fun getViewBinding(): ActivityModifyArticleBinding =
        ActivityModifyArticleBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()
    private val eventBus by inject<EventBus>()

    private val articleId by lazy { intent.getStringExtra(ARTICLE_ID)!! }

    private lateinit var postInfo: ModifyArticleDTO
    private val files: ArrayList<MultipartBody.Part> = ArrayList()
    private var statusRadioText: String = DEFAULT_STATUS
    private var sellingLocation: String = DEFAULT_LOCATION
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
        if (imageModelList.elementAt(removeIndex).articleImage?.contains("https") == true) {
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
        enterKeyboard()
        initButton()
    }

    private fun initButton() = with(binding) {
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

    private fun enterKeyboard() = with(binding) {
        binding.articlePriceTextView.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                locationClicked()
                true
            } else {
                false
            }
        }
    }

    private fun selectMultipleImage() {
        val currentSize = imageUriList.size

        TedImagePicker.with(this)
            .max(PostArticleActivity.MAX_IMAGE_NUM - currentSize, getString(R.string.maxImageNum))
            .mediaType(MediaType.IMAGE)
            .startMultiImage { uriList ->

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
                                imageUriList.add(Uri.parse(absolutePathOfImage).toString())
                                cursor.close()
                            }
                        }
                        addRecyclerViewList(imageUriList)
                    }
                } else {
                    imageUriList.addAll(uriList.map { it.path.toString() })
                    addRecyclerViewList(imageUriList)
                }

//                val filePathColumn =
//                    arrayOf(
//                        MediaStore.MediaColumns.DATA
//                    )
//
//                for (i in uriList.indices) {
//
//                    val cursor: Cursor? = this.contentResolver.query(
//                        uriList[i],
//                        filePathColumn,
//                        null,
//                        null,
//                        null
//                    )
//
//                    cursor?.let {
//                        if (cursor.moveToFirst()) {
//                            val columnIndex: Int =
//                                cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
//                            val absolutePathOfImage: String = cursor.getString(columnIndex)
//
//                            imageUriList.add(absolutePathOfImage)
//
//                            cursor.close()
//                        }
//                    }
//                    addRecyclerViewList(imageUriList)
//                }
            }
    }

    private fun addRecyclerViewList(uriList: List<String>) = with(binding) {
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

        val items = arrayOf(
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????",
            "??????"
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.location))
            .setItems(items) { _, which ->
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

    private fun postArticle() {
        if (checkInputInfo()) {

            uploadPhoto()
            uploadDesc()

            lifecycleScope.launch {
                viewModel.uploadArticle(articleId.toInt(), files, postInfo).join()
            }
        }
    }

    private fun checkInputInfo(): Boolean = with(binding) {
        when {
            imageModelList.isEmpty() -> {
                Toast.makeText(
                    this@ModifyArticleActivity,
                    getString(R.string.inputError_image),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            articleNameTextView.text.isEmpty() -> {
                Toast.makeText(
                    this@ModifyArticleActivity,
                    getString(R.string.inputError_title),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            articlePriceTextView.text.isEmpty() -> {
                Toast.makeText(
                    this@ModifyArticleActivity,
                    getString(R.string.inputError_price),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            locationTextView.text.isEmpty() -> {
                Toast.makeText(
                    this@ModifyArticleActivity,
                    getString(R.string.inputError_location),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            statusRadioText.isEmpty() -> {
                Toast.makeText(
                    this@ModifyArticleActivity,
                    getString(R.string.inputError_status),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            descriptionTextView.text.isEmpty() -> {
                Toast.makeText(
                    this@ModifyArticleActivity,
                    getString(R.string.inputError_description),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            else -> return true
        }
    }

    private fun uploadPhoto() {
        for (element in imageModelList) {
            if (element.articleImage?.contains("https") == false) {
                val file = File(element.articleImage!!)
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("images", file.name, requestBody)
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
    }

    override fun observeData() {
        viewModel.modifyArticleStateLiveData.observe(this) {
            when (it) {
                is ModifyArticleState.Uninitialized -> Unit
                is ModifyArticleState.Loading -> handleLoading()
                is ModifyArticleState.LoadSuccess -> handleLoadSuccess(it)
                is ModifyArticleState.ModifySuccess -> handleSuccess()
                is ModifyArticleState.Error -> handleError(it)
            }
        }
    }

    private fun handleLoading() = with(binding) {
        progressBar.isVisible = true
    }

    private fun handleSuccess() = with(binding) {
        progressBar.isGone = true
        lifecycleScope.launch {
            eventBus.produceEvent(Events.UploadPostEvent)
        }
        finish()
    }

    private fun handleLoadSuccess(state: ModifyArticleState.LoadSuccess) = with(binding) {
        binding.progressBar.isGone = true

        articleNameTextView.setText(state.article.title)
        articlePriceTextView.setText(state.article.price)
        radioGroup.check(changeStatus(state.article.bookStatus))
        locationTextView.text = state.article.sellingLocation
        sellingLocation = mappingLocation(state.article.sellingLocation)
        descriptionTextView.setText(state.article.description)
        officialBookNameTextView.text = state.article.bookResponse.bookTitle
        officialBookImageView.isVisible = true
        officialBookImageView.load(state.article.bookResponse.bookThumbnail, DEFAULT_IMAGEVIEW_RADIUS, CenterCrop())
        officialWriterTextView.text = getString(
            R.string.writer,
            state.article.bookResponse.bookAuthor.replace("<b>", "").replace("</b>", "")
        )
        officialPublisherTextView.text = getString(
            R.string.publisher,
            state.article.bookResponse.bookPublisher.replace("<b>", "").replace("</b>", "")
        )
        officialPriceTextView.text = getString(
            R.string.price,
            state.article.bookResponse.bookListPrice.replace("<b>", "").replace("</b>", "")
        )
        officialBookNameTextView.setTextColor(
            ContextCompat.getColor(
                this@ModifyArticleActivity,
                R.color.black
            )
        )
        officialWriterTextView.setTextColor(
            ContextCompat.getColor(
                this@ModifyArticleActivity,
                R.color.black
            )
        )
        officialPublisherTextView.setTextColor(
            ContextCompat.getColor(
                this@ModifyArticleActivity,
                R.color.black
            )
        )
        officialPriceTextView.setTextColor(
            ContextCompat.getColor(
                this@ModifyArticleActivity,
                R.color.black
            )
        )

        for (i in 0 until state.article.images.size)
            imageUriList.add(state.article.images[i].articleImage.toString())

        addRecyclerViewList(imageUriList)
    }

    private fun handleError(state: ModifyArticleState.Error) = with(binding) {
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
            viewModel.uploadArticle(articleId.toInt(), files, postInfo)
        }
    }

    private fun changeStatus(now: String): Int {
        when (now) {
            "??????" -> {
                statusRadioText = "BEST"
                return R.id.radio_best
            }
            "???" -> {
                statusRadioText = "UPPER"
                return R.id.radio_high
            }
            "???" -> {
                statusRadioText = "MIDDLE"
                return R.id.radio_mid
            }
            "???" -> {
                statusRadioText = "LOWER"
                return R.id.radio_low
            }
        }
        return R.id.radio_high
    }

    companion object {

        fun newIntent(context: Context, articleId: String) =
            Intent(context, ModifyArticleActivity::class.java).apply {
                putExtra(ARTICLE_ID, articleId)
            }

        const val ARTICLE_ID = "0"

        const val DEFAULT_STATUS = "UPPER"
        const val DEFAULT_LOCATION = "SEOUL"
    }
}
