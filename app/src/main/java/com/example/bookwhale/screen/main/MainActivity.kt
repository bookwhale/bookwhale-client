package com.example.bookwhale.screen.main

import android.content.Context
import android.content.Intent
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

    override fun initViews(): Unit = with(binding) {
        setSupportActionBar(toolBar)
        initBottomNav()
        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
        supportActionBar?.let {
            title = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.toolbar_menu, menu)
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

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }


}