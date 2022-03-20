package com.example.bookwhale.screen.article

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivitySearchBinding
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.NaverPagingAdapter
import com.example.bookwhale.widget.listener.main.article.NaverBookListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    override val viewModel by viewModel<SearchViewModel>()

    override fun getViewBinding(): ActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        NaverPagingAdapter(
            resourcesProvider = resourcesProvider,
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
        viewModel.searchStateLiveData.observe(this) {
            when(it) {
                is SearchState.Uninitialized -> Unit
                is SearchState.Loading -> handleLoading()
                is SearchState.Success -> handleSuccess()
                is SearchState.Error -> handleError(it)
            }
        }
    }

    private fun handleLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleSuccess() {
        binding.progressBar.isGone = true
    }

    private fun handleError(state: SearchState.Error) {
        binding.progressBar.isGone = true
        when(state.code!!) {
            "T_004" -> handleT004() // AccessToken 만료 코드
            else -> handleUnexpected(state.code)
        }
    }

    private fun handleT004() {
        lifecycleScope.launch {
            viewModel.getNewTokens().join()
            search(binding.searchEditText.text.toString())
        }
    }

    private fun handleUnexpected(code: String) {
        Toast.makeText(this, getString(R.string.error_unKnown, code), Toast.LENGTH_SHORT).show()
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