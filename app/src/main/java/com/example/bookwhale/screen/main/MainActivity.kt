package com.example.bookwhale.screen.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityMainBinding
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.main.chat.ChatFragment
import com.example.bookwhale.screen.main.home.HomeFragment
import com.example.bookwhale.screen.main.favorite.FavoriteFragment
import com.example.bookwhale.screen.main.my.MyFragment
import com.example.bookwhale.screen.main.mypost.MyPostFragment
import com.example.bookwhale.util.EventBus
import com.example.bookwhale.util.Events
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel by viewModel<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private var onSearch = false
    private val eventBus by inject<EventBus>()
    private val disposable = CompositeDisposable() // Disposable 관리
    private val backBtnSubject = PublishSubject.create<Boolean>() // backBtn 이벤트를 발생시킬 수 있는 Subject

    override fun initViews(): Unit = with(binding) {
        initBottomNav()
        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
        initButton()
        viewModel.getMyInfo()
    }

    private fun initButton() = with(binding) {
        searchButton.setOnClickListener {
            when(onSearch) {
                true -> {
                    onSearch = false
                    toolBarLayout.transitionToStart()
                    lifecycleScope.launch {
                        doSearch()
                    }
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
                    binding.searchButton.isVisible = true
                    true
                }
                R.id.menu_heart -> {
                    showFragment(FavoriteFragment.newInstance(), FavoriteFragment.TAG)
                    viewModel.getFavorites()
                    binding.searchButton.isGone = true
                    true
                }
                R.id.menu_myPost -> {
                    showFragment(MyPostFragment.newInstance(), MyPostFragment.TAG)
                    viewModel.getMyArticles()
                    binding.searchButton.isGone = true
                    true
                }
                R.id.menu_chat -> {
                    showFragment(ChatFragment.newInstance(), ChatFragment.TAG)
                    viewModel.loadChatList()
                    binding.searchButton.isGone = true
                    true
                }
                R.id.menu_my -> {
                    showFragment(MyFragment.newInstance(), MyFragment.TAG)
                    binding.searchButton.isGone = true
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
    override fun observeData()  {

    }

    private suspend fun doSearch() = with(binding) {
        (supportFragmentManager.findFragmentByTag(HomeFragment.TAG) as HomeFragment).getArticles(searchEditText.text.toString())

        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
        searchEditText.text.clear()
    }

    override fun onBackPressed() {
        disposable.add(backBtnSubject
            .debounce(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                Toast.makeText(this, R.string.finishInfo, Toast.LENGTH_SHORT)
                    .show()
            }
            .timeInterval(TimeUnit.MILLISECONDS)
            .skip(1)
            .filter { interval ->
                interval.time() < BACK_BTN_EXIT_TIMEOUT
            }
            .subscribe {
                finishActivity(0)
                exitProcess(0)
            })

        backBtnSubject.onNext(true)
    }

    override fun onPause() {
        super.onPause()

        disposable.clear()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)

        const val BACK_BTN_EXIT_TIMEOUT = 2000 // 연속된 Back 버튼의 시간 간격 (2초안에 백버튼 2번 클릭시 앱 종료)
    }


}