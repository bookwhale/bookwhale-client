package com.example.bookwhale.screen.article

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityDetailArticleBinding
import com.example.bookwhale.databinding.ActivityPostArticleBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class PostArticleActivity : BaseActivity<PostArticleViewModel, ActivityPostArticleBinding>() {

    override val viewModel by viewModel<PostArticleViewModel>()

    override fun getViewBinding(): ActivityPostArticleBinding = ActivityPostArticleBinding.inflate(layoutInflater)

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

    override fun observeData() {
        //
    }

    companion object {

        fun newIntent(context: Context) = Intent(context, PostArticleActivity::class.java)

    }
}