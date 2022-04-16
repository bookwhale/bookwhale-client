package com.example.bookwhale.screen.main

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
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
import com.example.bookwhale.screen.chatroom.ChatRoomActivity
import com.example.bookwhale.screen.main.chat.ChatFragment
import com.example.bookwhale.screen.main.home.HomeFragment
import com.example.bookwhale.screen.main.favorite.FavoriteFragment
import com.example.bookwhale.screen.main.my.MyFragment
import com.example.bookwhale.screen.main.mypost.MyPostFragment
import com.example.bookwhale.util.EventBus
import com.example.bookwhale.util.Events
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

    private val roomId by lazy { intent.getStringExtra(ROOM_ID) }
    private var searchStatus = SearchStatus.SEARCH_NOT
    private val messageChannel by inject<MessageChannel>()
    private val disposable = CompositeDisposable() // Disposable 관리
    private val backBtnSubject = PublishSubject.create<Boolean>() // backBtn 이벤트를 발생시킬 수 있는 Subject

    override fun initViews(): Unit = with(binding) {
        initBottomNav()
        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
        initButton()
        subscribeMessageChannel()
        viewModel.getMyInfo()

        roomId?.let { passToChatRoom() }
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
            when(searchStatus) {
                SearchStatus.SEARCH_ING -> {
                    searchStatus = SearchStatus.SEARCH_NOT
                    backButton.isGone = true
                    toolBarLayout.transitionToStart()
                    lifecycleScope.launch {
                        val queryString = searchEditText.text.toString()
                        doSearch(queryString)
                        searchStatus = SearchStatus.SEARCH_DONE
                        backButton.isVisible = true
                    }
                }

                false -> {
                    onSearch = true
                    keyboardHandle(handle = false)

                SearchStatus.SEARCH_NOT -> {
                    searchStatus = SearchStatus.SEARCH_ING
                    backButton.isVisible = true
                    toolBarLayout.transitionToEnd()
                }
                SearchStatus.SEARCH_DONE -> {
                    searchStatus = SearchStatus.SEARCH_ING
                    backButton.isVisible = true
                    toolBarLayout.transitionToEnd()
                }
            }
        }

        backButton.setOnClickListener {
            if(searchStatus == SearchStatus.SEARCH_ING) {
                searchStatus = SearchStatus.SEARCH_NOT
                backButton.isGone = true
                toolBarLayout.transitionToStart()
                keyboardHandle(handle = true)
            } else if (searchStatus == SearchStatus.SEARCH_DONE) {
                lifecycleScope.launch {
                    doSearch("")
                }
                backButton.isGone = true
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
                for (data in messageChannel.channel) {
                    Log.i("message Received: ", data.toString())

                    binding.popupArticleTitleTextview.text = data.title
                    binding.popupMessageTextView.text = data.message

                    binding.moveChatRoomButton.setOnClickListener {
                        data.roomId?.let {
                            startActivity(ChatRoomActivity.newIntent(this@MainActivity, it))
                        }
                    }
                    showPopupAnimation()
                }
            }
        }
    }

    private fun keyboardHandle(handle : Boolean){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(handle){//내리기
            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }
        else{//올리기
            imm.showSoftInput(binding.searchEditText, 0)
        }

    private suspend fun showPopupAnimation() = withContext(Dispatchers.Main) {
        binding.parentCardView.transitionToEnd() // 상단에 ui를 보여주는 애니메이션
        delay(3000L) // 3초간 나타난다
        binding.parentCardView.transitionToStart() // ui 없애는 애니메이션
        delay(500L)
    }

    private fun clearAnimation() {
        binding.parentCardView.transitionToStart()
    }

    override fun observeData() {
    }

    private suspend fun doSearch(query: String) = with(binding) {
        (supportFragmentManager.findFragmentByTag(HomeFragment.TAG) as HomeFragment).getArticles(query)

        showFragment(HomeFragment.newInstance(), HomeFragment.TAG)
        searchEditText.text.clear()
        keyboardHandle(handle = true)
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

    private fun passToChatRoom() {
        val intent = ChatRoomActivity.newIntent(this@MainActivity, roomId as String)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()

        disposable.clear()
        clearAnimation()
    }

    override fun onResume() {
        super.onResume()


    }

    companion object {
        fun newIntent(context: Context, roomId: String? = null) = Intent(context, MainActivity::class.java).apply {
            putExtra(ROOM_ID, roomId)
        }

        const val ROOM_ID = "roomId"
        const val BACK_BTN_EXIT_TIMEOUT = 2000 // 연속된 Back 버튼의 시간 간격 (2초안에 백버튼 2번 클릭시 앱 종료)
        const val TAG = "MainActivity"

        enum class SearchStatus {
            SEARCH_NOT,
            SEARCH_ING,
            SEARCH_DONE
        }
    }
}