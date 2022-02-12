package com.example.bookwhale.screen.article

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.example.bookwhale.databinding.ActivityPostArticleBinding
import com.example.bookwhale.databinding.ActivitySearchBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.main.MainActivity
import com.example.bookwhale.util.NaverPagingAdapter
import com.example.bookwhale.util.PagingAdapter
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.detail.NaverBookListener
import com.example.bookwhale.widget.listener.main.home.ArticleListListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    override val viewModel by viewModel<SearchViewModel>()
    override fun getViewBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)
    private val adapter by lazy {
        NaverPagingAdapter(
            adapterListener = object : NaverBookListener {
                override fun onClickItem(model: NaverBookModel) {
                    val intent = PostArticleActivity.newIntent(this@SearchActivity, model)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
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

    }
}