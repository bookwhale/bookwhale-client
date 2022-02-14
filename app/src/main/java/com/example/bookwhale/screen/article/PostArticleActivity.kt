package com.example.bookwhale.screen.article

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.MyApp
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
import com.example.bookwhale.widget.listener.AdapterListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class PostArticleActivity : BaseActivity<PostArticleViewModel, ActivityPostArticleBinding>(), HandlePathOzListener.MultipleUri {

    override val viewModel by viewModel<PostArticleViewModel>()

    override fun getViewBinding(): ActivityPostArticleBinding = ActivityPostArticleBinding.inflate(layoutInflater)

    private val naverBookInfo by lazy { intent.getParcelableExtra<NaverBookModel>(NAVER_BOOK_INFO)}

    private val resourcesProvider by inject<ResourcesProvider>()

    private val files: ArrayList<MultipartBody.Part> = ArrayList()
    private var statusRadioText : String = "UPPER"
    private var sellingLocation : String = "SEOUL"

    private lateinit var handlePathOz: HandlePathOz

    private val adapter by lazy {
        ModelRecyclerAdapter<DetailImageModel, PostArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : AdapterListener {

            }
        )
    }

    override fun initViews() {

        binding.recyclerView.adapter = adapter

        initButton()
        handleNaverBookApi()
        initHandlePathOz()

        //test()

    }

    @FlowPreview
    private fun initButton() = with(binding) {
        officialBookNameTextView.setOnClickListener {
            startActivity(SearchActivity.newIntent(this@PostArticleActivity))
        }
        officialBookImageLayout.setOnClickListener {
            startActivity(SearchActivity.newIntent(this@PostArticleActivity))
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

        TedImagePicker.with(this)
            .max(MAX_IMAGE_NUM, getString(R.string.maxImageNum))
            .startMultiImage { uriList ->
                Log.e("uriList?", uriList.toString())
                handlePathOz.getListRealPath(uriList)
            }
    }

    private fun initHandlePathOz() {
        handlePathOz = HandlePathOz(MyApp.appContext!!, this)
    }
    override fun onRequestHandlePathOz(listPathOz: List<PathOz>, tr: Throwable?): Unit = with(binding) {

        // 파일 경로들을 가지고있는 `ArrayList<Uri> filePathList`가 있다고 칩시다...
        for (element in listPathOz) {
            // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성

            var file = File(element.path)
            var requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            var body : MultipartBody.Part = MultipartBody.Part.createFormData("images",file.name,requestBody)

            // 추가
            files.add(body)
        }

//        viewModel.testUpload(files)
//
        adapter.submitList(listPathOz.map {
            DetailImageModel(
                id = it.hashCode().toLong(),
                type = CellType.TEMP_IMAGE_LIST,
                articleImage = it.path
            )
        })

        val count = listPathOz.size
        uploadPhotoTextView.text = getString(R.string.currentImageNum, count)

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

        var hasFilled = true

        naverBookInfo?.let {
            if(articleNameTextView.text.isNullOrEmpty()) {
                Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_title), Toast.LENGTH_SHORT).show()
                hasFilled = false
            }
            else if(articlePriceTextView.text.isNullOrEmpty()) {
                Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_price), Toast.LENGTH_SHORT).show()
                hasFilled = false
            }
            else if(locationTextView.text.isNullOrEmpty()) {
                Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_location), Toast.LENGTH_SHORT).show()
                hasFilled = false
            }
            else if(statusRadioText.isNullOrEmpty()) {
                Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_status), Toast.LENGTH_SHORT).show()
                hasFilled = false
            }
            else if(descriptionTextView.text.isNullOrEmpty()) {
                Toast.makeText(this@PostArticleActivity, getString(R.string.inputError_description), Toast.LENGTH_SHORT).show()
                hasFilled = false
            }
        }?: run {
            Toast.makeText(this@PostArticleActivity, getString(R.string.searchBookName), Toast.LENGTH_SHORT).show()
            hasFilled = false
        }

        if (hasFilled) {
            val postInfo = ArticleDTO(
                bookRequest = ArticleDTO.BookRequest(
                    bookIsbn = naverBookInfo!!.bookIsbn,
                    bookTitle = naverBookInfo!!.bookTitle,
                    bookAuthor = naverBookInfo!!.bookAuthor,
                    bookPublisher = naverBookInfo!!.bookPublisher,
                    bookThumbnail = naverBookInfo!!.bookThumbnail,
                    bookListPrice = naverBookInfo!!.bookListPrice,
                    bookPubDate = "none", // 수정요청
                    bookSummary = naverBookInfo!!.bookSummary
                ),
                title = articleNameTextView.text.toString(),
                price = articlePriceTextView.text.toString(),
                description = descriptionTextView.text.toString(),
                bookStatus = statusRadioText!!,
                sellingLocation = sellingLocation,
            )

            lifecycleScope.launch {
                viewModel.uploadArticle(files, postInfo).join()
                finish()
            }
        }
    }
    override fun observeData() {
        //
    }

    companion object {

        fun newIntent(context: Context, bookInfo: NaverBookModel?) = Intent(context, PostArticleActivity::class.java).apply {
            putExtra(NAVER_BOOK_INFO, bookInfo)
        }

        const val NAVER_BOOK_INFO = "NaverBookInfo"
        const val MAX_IMAGE_NUM = 5
    }

}