package com.example.bookwhale.screen.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityMainBinding
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.main.home.HomeFragment
import com.example.bookwhale.screen.main.lielist.LikeListFragment
import com.example.bookwhale.screen.main.my.MyFragment
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel by viewModel<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private var onSearch = false

    override fun initViews(): Unit = with(binding) {
        initBottomNav()
        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
        initButton()
    }

    private fun initButton() = with(binding) {
        searchButton.setOnClickListener {
            when(onSearch) {
                true -> {
                    onSearch = false
                    getSearchArticles(searchEditText.text.toString())
                    toolBarLayout.transitionToStart()
                }
                false -> {
                    onSearch = true
                    toolBarLayout.transitionToEnd()
                }
            }
        }

        backButton.setOnClickListener {
            if(onSearch) {
                onSearch = false
                toolBarLayout.transitionToStart()
            }
        }
    }


    private fun initBottomNav() = with(binding) {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
                    true
                }
                R.id.menu_heart -> {
                    showFragment(LikeListFragment.newInstance(), LikeListFragment.TAG)
                    true
                }
                R.id.menu_myPost -> {
                    true
                }
                R.id.menu_chat -> {
                    true
                }
                R.id.menu_my -> {
                    showFragment(MyFragment.newInstance(), MyFragment.TAG)
                    true
                }
                else -> false
            }
        }
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        val findFragment = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.fragments.forEach { fm ->
            supportFragmentManager.beginTransaction().hide(fm).commitAllowingStateLoss()
        }

        findFragment?.let {
            supportFragmentManager.beginTransaction().show(it).commitAllowingStateLoss()
        } ?: kotlin.run {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, fragment, tag)
                .commitAllowingStateLoss()
        }
    }

    private fun handleSearch() {}
    private fun handleNotification() {}


    override fun observeData()  {

    }

    private fun getSearchArticles(searchText: String) {
        viewModel.getArticles(searchText,PAGE, SIZE)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)

        const val PAGE = 0
        const val SIZE = 10
    }


}