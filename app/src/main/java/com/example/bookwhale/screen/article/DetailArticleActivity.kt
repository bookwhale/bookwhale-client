package com.example.bookwhale.screen.article

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityDetailArticleBinding
import com.example.bookwhale.databinding.ActivityTestBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.main.MainActivity
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.screen.test.TestViewModel
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class DetailArticleActivity : BaseActivity<DetailArticleViewModel, ActivityDetailArticleBinding>() {

    override val viewModel by viewModel<DetailArticleViewModel>()

    override fun getViewBinding(): ActivityDetailArticleBinding = ActivityDetailArticleBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val articleId by lazy { intent.getStringExtra(ARTICLE_ID)!! }

    private val adapter by lazy {
        ModelRecyclerAdapter<DetailImageModel, DetailArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : AdapterListener {
            }
        )
    }



    private var isEnd = false

    override fun initViews() = with(binding) {

        recyclerView.adapter = adapter
        viewModel.loadArticle(articleId.toInt())

        initButton()

    }

    private fun initButton() = with(binding) {

        arrowUpAndDown.setOnClickListener {

            val currentDegree = arrowUpAndDown.rotation
            ObjectAnimator.ofFloat(arrowUpAndDown, View.ROTATION, currentDegree, currentDegree + 180f) .setDuration(200) .start()

            if(isEnd) {
                layout.transitionToStart()
                officialLayout.transitionToStart()
                isEnd = false
            } else {
                layout.transitionToEnd()
                officialLayout.transitionToEnd()
                isEnd = true
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun observeData() {
        viewModel.detailArticleStateLiveData.observe(this) {
            when (it) {
                is DetailArticleState.Uninitialized -> Unit
                is DetailArticleState.Success -> handleSuccess(it)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccess(state: DetailArticleState.Success) = with(binding) {

        articleTitle.text = state.article.title
        articlePriceTextView.text = "${state.article.price}원"
        qualityTextView.text = state.article.bookStatus
        locationTextView.text = state.article.sellingLocation
        descriptionTextView.text = state.article.description
        heartTextView.text = state.article.favoriteCount.toString()
        viewTextView.text = state.article.viewCount.toString()
        officialBookNameTextView.text = state.article.bookResponse.bookTitle
        officialBookImageView.load(state.article.bookResponse.bookThumbnail)
        officialPriceTextView.text = "가격 ${state.article.bookResponse.bookListPrice}원"
        officialWriterTextView.text = "글 ${state.article.bookResponse.bookAuthor}"
        officialPublisherTextView.text = "출판사 ${state.article.bookResponse.bookPublisher}"

        adapter.submitList(state.article.images)
    }

    companion object {

        fun newIntent(context: Context, articleId: String) = Intent(context, DetailArticleActivity::class.java).apply {
            putExtra(ARTICLE_ID, articleId)
        }

        const val ARTICLE_ID = "0"

    }
}