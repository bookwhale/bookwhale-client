package com.example.bookwhale.screen.article

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.R
import com.example.bookwhale.data.response.chat.MakeChatDTO
import com.example.bookwhale.databinding.ActivityDetailArticleBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.chatroom.ChatRoomActivity
import com.example.bookwhale.screen.main.MainActivity
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.screen.main.favorite.FavoriteState
import com.example.bookwhale.screen.main.home.HomeFragment
import com.example.bookwhale.screen.test.TestViewModel
import com.example.bookwhale.util.OnSingleClickListener
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.scope.scope
import org.koin.android.viewmodel.ext.android.viewModel

class DetailArticleActivity : BaseActivity<DetailArticleViewModel, ActivityDetailArticleBinding>() {

    override val viewModel by viewModel<DetailArticleViewModel>()

    override fun getViewBinding(): ActivityDetailArticleBinding = ActivityDetailArticleBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val articleId by lazy { intent.getStringExtra(ARTICLE_ID)!! }

    private val adapter by lazy {
        ModelRecyclerAdapter<DetailImageModel, DetailArticleViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : AdapterListener {
            }
        )
    }

    private var isEnd = false
    private var myFavorite = false
    private var myArticle = false
    private var favoriteId = 0
    private var sellerId = 0

    override fun initViews(): Unit = with(binding) {

        recyclerView.adapter = adapter

        viewModel.loadArticle(articleId.toInt())
        viewModel.loadFavorites()

        initButton()
        observeChatData()

    }

    private fun initButton() = with(binding) {

        handleArrowButton()

        backButton.setOnClickListener {
            finish()
        }


        chatLayout.setOnClickListener {
            viewModel.makeNewChat(MakeChatDTO(
                articleId = articleId.toInt(),
                sellerId = sellerId
            ))
        }

    }

    private fun handleArrowButton() = with(binding) {
        arrowUpAndDown.setOnClickListener {

            val currentDegree = arrowUpAndDown.rotation
            ObjectAnimator.ofFloat(arrowUpAndDown, View.ROTATION, currentDegree, currentDegree + 180f) .setDuration(200) .start()

            if(isEnd) {
                layout.transitionToStart()
                officialLayout.transitionToStart()
                isEnd = false
            } else {
                layout.transitionToEnd()
                officialLayout.transitionToEnd()
                isEnd = true
            }
        }
    }

    private fun handleHeartButton() = with(binding) {

        unFilledHeartButton.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                if(myFavorite) {
                    lifecycleScope.launch {
                        val response = viewModel.deleteFavorite(favoriteId).await()
                        if(response) {
                            unFilledHeartButton.setImageResource(R.drawable.ic_heart)
                            myFavorite = false
                        } else {
                            Toast.makeText(this@DetailArticleActivity, getString(R.string.error_noFavorite), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    lifecycleScope.launch {
                        favoriteId = viewModel.addFavorite(articleId.toInt()).await()
                        if(favoriteId == 0) {
                            Toast.makeText(this@DetailArticleActivity, getString(R.string.error_noFavorite), Toast.LENGTH_SHORT).show()
                        } else {
                            unFilledHeartButton.setImageResource(R.drawable.ic_heart_filled)
                            myFavorite = true
                        }
                    }
                }
            }

        })
    }

    override fun observeData() {
        viewModel.detailArticleStateLiveData.observe(this) {
            when (it) {
                is DetailArticleState.Uninitialized -> Unit
                is DetailArticleState.Loading -> handleLoading()
                is DetailArticleState.Success -> handleSuccess(it)
                is DetailArticleState.FavoriteSuccess -> handleGetFavorite(it)
                is DetailArticleState.Error -> handleError(it)
            }
        }
    }

    private fun observeChatData() {
        viewModel.loadChatListLiveData.observe(this) {
            when(it) {
                false -> Unit
                true -> Toast.makeText(this@DetailArticleActivity, "이미 존재하는 채팅방 입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLoading() = with(binding) {
        binding.progressBar.isVisible = true
    }

    private fun handleGetFavorite(state: DetailArticleState.FavoriteSuccess) = with(binding) {
        binding.progressBar.isGone = true

        state.favoriteList.forEach { // 현재 게시물 favoriteId 찾아오기
            if(it.articleId.toString() == articleId) favoriteId = it.favoriteId
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleSuccess(state: DetailArticleState.Success) =with(binding){
        binding.progressBar.isGone = true

        articleTitle.text = state.article.title
        articleTitle.visibility = View.GONE
        articlePriceTextView.text = "${state.article.price}원"
        qualityTextView.text = state.article.bookStatus
        locationTextView.text = state.article.sellingLocation
        descriptionTextView.text = state.article.description
        viewTextView.text = state.article.viewCount.toString()
        officialBookNameTextView.text = state.article.bookResponse.bookTitle
        officialBookImageView.load(state.article.bookResponse.bookThumbnail)
        officialPriceTextView.text = "${state.article.bookResponse.bookListPrice}원"
        officialWriterTextView.text = "글 ${state.article.bookResponse.bookAuthor}"
        officialPublisherTextView.text = "출판 ${state.article.bookResponse.bookPublisher}"

        myFavorite = state.article.myFavorite
        state.article.myFavoriteId?.let { favoriteId = it }
        myArticle = state.article.myArticle
        sellerId = state.article.sellerId

        if(myFavorite) {
            unFilledHeartButton.setImageResource(R.drawable.ic_heart_filled)
        }
        else unFilledHeartButton.setImageResource(R.drawable.ic_heart)

        if(myArticle) {
            Log.e("myArticle","myArticle")
            chatLayout.isGone = true
        } else {
            Log.e("myArtic222le","my222Article")
            chatLayout.isVisible = true
        }

        adapter.submitList(state.article.images)
        //if(state.article.images.isEmpty()) adapter.submitList(listOf(DetailImageModel(id = 0, arti)))

        handleHeartButton()
    }

    private fun handleError(state: DetailArticleState.Error) = with(binding) {
        binding.progressBar.isGone = true

        when(state.code!!) {
            "T_004" -> handleT004() // AccessToken 만료 코드
        }
    }

    private fun handleT004() {
        lifecycleScope.launch {
            viewModel.getNewTokens().join()
            viewModel.loadArticle(articleId.toInt())
        }
    }

    companion object {

        fun newIntent(context: Context, articleId: String) = Intent(context, DetailArticleActivity::class.java).apply {
            putExtra(ARTICLE_ID, articleId)
        }

        const val ARTICLE_ID = "0"
    }
}