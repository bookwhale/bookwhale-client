package com.example.bookwhale.screen.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityMainBinding
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.main.chat.ChatFragment
import com.example.bookwhale.screen.main.home.HomeFragment
import com.example.bookwhale.screen.main.favorite.FavoriteFragment
import com.example.bookwhale.screen.main.my.MyFragment
import com.example.bookwhale.screen.main.mypost.MyPostFragment
import com.example.bookwhale.util.MessageChannel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel by viewModel<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private var onSearch = false
    private val messageChannel by inject<MessageChannel>()
    private val disposable = CompositeDisposable() // Disposable 관리
    private val backBtnSubject = PublishSubject.create<Boolean>() // backBtn 이벤트를 발생시킬 수 있는 Subject

    override fun initViews(): Unit = with(binding) {
        initBottomNav()
        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
        initButton()
        subscribeMessageChannel()
        viewModel.getMyInfo()
    }

    private fun initButton() = with(binding) {
        searchEditText.setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                lifecycleScope.launch {
                    doSearch()
                }
                true
            } else {
                false
            }
        }
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

    private fun subscribeMessageChannel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                messageChannel.mutex.withLock {
                    for (i in messageChannel.channel) {
                        Log.i("message Received: ", i)

                        viewModel.loadPopupData()

                        withContext(Dispatchers.Main) {
                            binding.parentCardView.transitionToEnd() // 상단에 ui를 보여주는 애니메이션
                        }
                        delay(3000L) // 3초간 나타난다
                        withContext(Dispatchers.Main) {
                            binding.parentCardView.transitionToStart() // ui 없애는 애니메이션
                        }
                        delay(500L)
                    }
                }
            }
        }
    }

    override fun observeData() {
        viewModel.titleLiveData.observe(this@MainActivity) {
            binding.popupArticleTitleTextview.text = it
        }
        viewModel.messageLiveData.observe(this@MainActivity) {
            binding.popupMessageTextView.text = it
        }
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

        const val TAG = "MainActivity"
    }
}