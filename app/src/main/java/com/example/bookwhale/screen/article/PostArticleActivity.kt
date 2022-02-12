package com.example.bookwhale.screen.article

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityDetailArticleBinding
import com.example.bookwhale.databinding.ActivityPostArticleBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class PostArticleActivity : BaseActivity<PostArticleViewModel, ActivityPostArticleBinding>() {

    override val viewModel by viewModel<PostArticleViewModel>()

    override fun getViewBinding(): ActivityPostArticleBinding = ActivityPostArticleBinding.inflate(layoutInflater)

    private val naverBookInfo by lazy { intent.getParcelableExtra<NaverBookModel>(NAVER_BOOK_INFO)}

    //private val articleId by lazy { intent.getStringExtra(DetailArticleActivity.ARTICLE_ID)!! }

    private val resourcesProvider by inject<ResourcesProvider>()

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

        initButton()
        handleNaverBookApi()

    }

    private fun initButton() = with(binding) {
        officialBookNameTextView.setOnClickListener {
            startActivity(SearchActivity.newIntent(this@PostArticleActivity))
        }
        officialBookImageLayout.setOnClickListener {
            startActivity(SearchActivity.newIntent(this@PostArticleActivity))
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

    override fun observeData() {
        //
    }

    companion object {

        fun newIntent(context: Context, bookInfo: NaverBookModel?) = Intent(context, PostArticleActivity::class.java).apply {
            putExtra(NAVER_BOOK_INFO, bookInfo)
        }

        const val NAVER_BOOK_INFO = "NaverBookInfo"
    }
}