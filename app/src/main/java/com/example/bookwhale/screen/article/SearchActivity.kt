package com.example.bookwhale.screen.article

import android.content.Context
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.databinding.ActivitySearchBinding
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.widget.adapter.NaverPagingAdapter
import com.example.bookwhale.widget.listener.main.article.NaverBookListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    override val viewModel by viewModel<SearchViewModel>()
    override fun getViewBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)
    private val adapter by lazy {
        NaverPagingAdapter(
            adapterListener = object : NaverBookListener {
                override fun onClickItem(model: NaverBookModel) {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(NAVER_BOOK_MODEL, model)
                    })
                    finish()
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter

        initButton()
    }

    private fun initButton() = with(binding) {
        searchButton.setOnClickListener {
            search(searchEditText.text.toString())
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun observeData() {
        //
    }

    private fun search(title: String) {
        lifecycleScope.launch {
            viewModel.getNaverBookPaging(title).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    companion object {

        fun newIntent(context: Context) = Intent(context, SearchActivity::class.java)

        const val NAVER_BOOK_MODEL = "naverBookModel"

    }
}