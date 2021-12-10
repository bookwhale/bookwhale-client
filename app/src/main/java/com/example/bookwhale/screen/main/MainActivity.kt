package com.example.bookwhale.screen.main

import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityMainBinding
import com.example.bookwhale.model.main.ArticleModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.ArticleListListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel by viewModel<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<ArticleModel, MainViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : ArticleListListener {
                override fun onClickItem(model: ArticleModel) {
                    Toast.makeText(this@MainActivity,model.postPrice,Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

    override fun initViews(): Unit = with(binding) {
        setSupportActionBar(toolBar)
        supportActionBar?.let {
            title = null
        }

        recyclerView.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.toolbar_search -> handleSearch()
            R.id.toolbar_notification -> handleNotification()
            else -> Unit
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleSearch() {}
    private fun handleNotification() {}



    override fun observeData()  {
        viewModel.articleListLiveData.observe(this) {
            adapter.submitList(it)
        }
    }


}