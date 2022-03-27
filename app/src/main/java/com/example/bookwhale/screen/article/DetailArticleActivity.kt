package com.example.bookwhale.screen.article

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.R
import com.example.bookwhale.data.response.chat.MakeChatDTO
import com.example.bookwhale.databinding.ActivityDetailArticleBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.chatroom.ChatRoomActivity
import com.example.bookwhale.util.OnSingleClickListener
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class DetailArticleActivity : BaseActivity<DetailArticleViewModel, ActivityDetailArticleBinding>() {

    override val viewModel by viewModel<DetailArticleViewModel>()

    override fun getViewBinding(): ActivityDetailArticleBinding =
        ActivityDetailArticleBinding.inflate(layoutInflater)

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

        chatLayout.setOnClickListener { // 채팅방 개설
            lifecycleScope.launch {
                viewModel.makeNewChat(MakeChatDTO(
                    articleId = articleId.toInt(),
                    sellerId = sellerId
                )).join() // 채팅방이 다 개설되는것을 대기한 후 이동한다.

                startActivity(ChatRoomActivity.newIntent(this@DetailArticleActivity,
                    viewModel.roomId.value.toString()))
            }

        }

    }

    private fun handleArrowButton() = with(binding) {
        arrowUpAndDown.setOnClickListener {

            val currentDegree = arrowUpAndDown.rotation
            ObjectAnimator.ofFloat(arrowUpAndDown,
                View.ROTATION,
                currentDegree,
                currentDegree + 180f).setDuration(200).start()

            if (isEnd) {
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
                if (myFavorite) {
                    lifecycleScope.launch {
                        val response = viewModel.deleteFavoriteAsync(favoriteId).await()
                        if (response) {
                            unFilledHeartButton.setImageResource(R.drawable.ic_heart)
                            myFavorite = false
                        } else {
                            Toast.makeText(this@DetailArticleActivity,
                                getString(R.string.error_noFavorite),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    lifecycleScope.launch {
                        favoriteId = viewModel.addFavoriteAsync(articleId.toInt()).await()
                        if (favoriteId == 0) {
                            Toast.makeText(this@DetailArticleActivity,
                                getString(R.string.error_noFavorite),
                                Toast.LENGTH_SHORT).show()
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

    private fun handleLoading() = with(binding) {
        binding.progressBar.isVisible = true
    }

    private fun handleGetFavorite(state: DetailArticleState.FavoriteSuccess) = with(binding) {
        binding.progressBar.isGone = true

        state.favoriteList.forEach { // 현재 게시물 favoriteId 찾아오기
            if (it.articleId.toString() == articleId) favoriteId = it.favoriteId
        }
    }

    private fun handleSuccess(state: DetailArticleState.Success) = with(binding) {
        binding.progressBar.isGone = true

        Log.e("myArticle is what?",myArticle.toString())


        articleTitle.text = state.article.title
        articleTitle.visibility = View.GONE
        articlePriceTextView.text = getString(R.string.price, state.article.price)
        qualityTextView.text = state.article.bookStatus
        locationTextView.text = state.article.sellingLocation
        descriptionTextView.text = state.article.description
        viewTextView.text = state.article.viewCount.toString()
        officialBookNameTextView.text = state.article.bookResponse.bookTitle
        officialBookImageView.load(state.article.bookResponse.bookThumbnail)
        officialPriceTextView.text = getString(R.string.price, state.article.bookResponse.bookListPrice)
        officialWriterTextView.text = getString(R.string.writer, state.article.bookResponse.bookAuthor)
        officialPublisherTextView.text = getString(R.string.publisher, state.article.bookResponse.bookPublisher)

        myFavorite = state.article.myFavorite
        state.article.myFavoriteId?.let { favoriteId = it }
        myArticle = state.article.myArticle
        sellerId = state.article.sellerId

        if (myFavorite) {
            unFilledHeartButton.setImageResource(R.drawable.ic_heart_filled)
        } else unFilledHeartButton.setImageResource(R.drawable.ic_heart)

        Log.e("myArticle is what?",myArticle.toString())


        if (myArticle) {
            Log.e("myArticle1234","myArticle")
            chatLayout.isGone = true
            unFilledHeartButton.isGone = true
            modifyButton.isVisible = true
            modifyButton.setOnClickListener{
                startActivity(ModifyArticleActivity.newIntent(this@DetailArticleActivity, articleId))
                finish()
            }
        } else {
            chatLayout.isVisible = true
        }

        adapter.submitList(state.article.images)

        handleHeartButton()
    }

    private fun handleError(state: DetailArticleState.Error) = with(binding) {
        binding.progressBar.isGone = true

        Log.e("handleError",state.code.toString())

        when (state.code!!) {
            "T_004" -> handleT004() // AccessToken 만료 코드
            else -> handleUnexpected(state.code)
        }
    }

    private fun handleUnexpected(code: String) {
        Toast.makeText(this, getString(R.string.error_unKnown, code), Toast.LENGTH_SHORT).show()
    }

    private fun handleT004() {
        lifecycleScope.launch {
            viewModel.getNewTokens().join()
            viewModel.loadArticle(articleId.toInt())
        }
    }

    private fun observeChatData() {
        viewModel.loadChatListLiveData.observe(this) {
            when (it) {
                false -> Unit
                true -> Toast.makeText(this@DetailArticleActivity,
                    getString(R.string.alreadyExistRoom),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        fun newIntent(context: Context, articleId: String) =
            Intent(context, DetailArticleActivity::class.java).apply {
                putExtra(ARTICLE_ID, articleId)
            }

        const val ARTICLE_ID = "0"
    }
}