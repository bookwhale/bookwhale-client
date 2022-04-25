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
import androidx.lifecycle.viewModelScope
import com.example.bookwhale.R
import com.example.bookwhale.data.response.article.ArticleStatusCategory
import com.example.bookwhale.data.response.chat.MakeChatDTO
import com.example.bookwhale.databinding.ActivityDetailArticleBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.screen.chatroom.ChatRoomActivity
import com.example.bookwhale.util.EventBus
import com.example.bookwhale.util.Events
import com.example.bookwhale.util.OnSingleClickListener
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class DetailArticleActivity : BaseActivity<DetailArticleViewModel, ActivityDetailArticleBinding>() {

    override val viewModel by viewModel<DetailArticleViewModel>()

    override fun getViewBinding(): ActivityDetailArticleBinding =
        ActivityDetailArticleBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val articleId by lazy { intent.getStringExtra(ARTICLE_ID)!! }

    private val eventBus by inject<EventBus>()

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
    private var clicked: Boolean = false
    private var articleStatus : ArticleStatusCategory = ArticleStatusCategory.SALE

    override fun initViews(): Unit = with(binding) {

        recyclerView.adapter = adapter

        lifecycleScope.launch {
            val loadArticle = viewModel.loadArticle(articleId.toInt())
            val loadFavorite = viewModel.loadFavorites()

            joinAll(loadArticle, loadFavorite)

            initButton()
            subscribeEvent()
        }

    }

    private fun initButton() = with(binding) {

        backButton.setOnClickListener {
            finish()
        }

        arrowUpAndDown.setOnClickListener {

            val currentDegree = arrowUpAndDown.rotation
            ObjectAnimator.ofFloat(arrowUpAndDown,
                View.ROTATION,
                currentDegree,
                currentDegree + ARROW_ROTATE_DEGREE).setDuration(ARROW_ANIMATION_DURATION).start()

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

        chatButton.setOnClickListener { // 채팅방 개설
            lifecycleScope.launch {
                viewModel.makeNewChat(MakeChatDTO(
                    articleId = articleId.toInt(),
                    sellerId = sellerId
                )).join() // 채팅방이 다 개설되는것을 대기한 후 이동한다.

                startActivity(ChatRoomActivity.newIntent(this@DetailArticleActivity,
                    viewModel.roomId.value.toString()))
            }
        }

        modifyButton.setOnClickListener{
            startActivity(ModifyArticleActivity.newIntent(this@DetailArticleActivity, articleId))
        }

        deleteButton.setOnClickListener {
            showDialog(DialogCategory.DELETE)
        }

        reservedButton.setOnClickListener {
            if(articleStatus == ArticleStatusCategory.RESERVED) {
                showDialog(DialogCategory.ALREADY_RESERVED)
            } else {
                showDialog(DialogCategory.RESERVED)
            }
        }

        soldOutButton.setOnClickListener {
            if(articleStatus == ArticleStatusCategory.SOLD_OUT) {
                showDialog(DialogCategory.ALREADY_SOLD_OUT)
            } else {
                showDialog(DialogCategory.SOLD_OUT)
            }
        }

        dialButton.setOnClickListener {
            if(!clicked) {
                dialUp()
            } else {
                dialDown()
            }
        }

    }

    private fun showDialog(category: DialogCategory) {

        val message = when(category) {
            DialogCategory.DELETE -> resources.getString(R.string.delete_question)
            DialogCategory.RESERVED -> resources.getString(R.string.reserved_question)
            DialogCategory.SOLD_OUT -> resources.getString(R.string.soldOut_question)
            DialogCategory.ALREADY_RESERVED -> resources.getString(R.string.reserved_undo)
            DialogCategory.ALREADY_SOLD_OUT -> resources.getString(R.string.reserved_question)
        }

        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                // Respond to positive button press
                when(category) {
                    DialogCategory.RESERVED -> viewModel.updateStatus(articleId.toInt(), ArticleStatusCategory.RESERVED)
                    DialogCategory.SOLD_OUT -> viewModel.updateStatus(articleId.toInt(), ArticleStatusCategory.SOLD_OUT)
                    DialogCategory.DELETE -> viewModel.deleteArticle(articleId.toInt())
                    DialogCategory.ALREADY_RESERVED -> viewModel.updateStatus(articleId.toInt(), ArticleStatusCategory.SALE)
                }
            }
            .show()
    }

    private fun dialUp() = with(binding) {
        dialButton.animate().rotation(45f)
        modifyButton.animate().translationY(-resources.getDimension(R.dimen.modify))
        deleteButton.animate().translationY(-resources.getDimension(R.dimen.delete))
        reservedButton.animate().translationY(-resources.getDimension(R.dimen.reserved))
        soldOutButton.animate().translationY(-resources.getDimension(R.dimen.soldOut))
        clicked = true
    }

    private fun dialDown() = with(binding) {
        dialButton.animate().rotation(0f)
        modifyButton.animate().translationY(0f)
        deleteButton.animate().translationY(0f)
        reservedButton.animate().translationY(0f)
        soldOutButton.animate().translationY(0f)
        clicked = false
    }


    private fun subscribeEvent() {
        lifecycleScope.launch {
            eventBus.subscribeEvent(Events.UploadPostEvent) {
                viewModel.loadArticle(articleId.toInt())
                dialDown()
            }
        }

        lifecycleScope.launch {
            eventBus.subscribeEvent(Events.Reserved) {
                viewModel.loadArticle(articleId.toInt())
            }
        }
        lifecycleScope.launch {
            eventBus.subscribeEvent(Events.Sold) {
                viewModel.loadArticle(articleId.toInt())
            }
        }
        lifecycleScope.launch {
            eventBus.subscribeEvent(Events.Sale) {
                viewModel.loadArticle(articleId.toInt())
            }
        }
        lifecycleScope.launch {
            eventBus.subscribeEvent(Events.Deleted) {
                Toast.makeText(this@DetailArticleActivity, getString(R.string.deleted), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        lifecycleScope.launch {
            eventBus.subscribeEvent(Events.DeleteFail) {
                Toast.makeText(this@DetailArticleActivity, getString(R.string.deleted_fail), Toast.LENGTH_SHORT).show()
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

        if (myArticle) {
            chatButton.isGone = true
            buttonGroup.isVisible = true
            unFilledHeartButton.isGone = true
        } else {
            chatButton.isVisible = true
            buttonGroup.isGone = true
        }

        state.article.articleStatus.let { status ->
            articleStatusTextView.text = status

            when (status) {
                SALE_STRING -> articleStatus = ArticleStatusCategory.SALE
                RESERVED_STRING -> articleStatus = ArticleStatusCategory.RESERVED
                SOLD_OUT_STRING -> {
                    articleStatus = ArticleStatusCategory.SOLD_OUT
                    buttonGroup.isGone = true
                }
            }
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

    companion object {

        fun newIntent(context: Context, articleId: String) =
            Intent(context, DetailArticleActivity::class.java).apply {
                putExtra(ARTICLE_ID, articleId)
            }

        const val ARTICLE_ID = "0"
        const val SOLD_OUT_STRING = "판매완료"
        const val SALE_STRING = "판매중"
        const val RESERVED_STRING = "예약중"
        const val ARROW_ANIMATION_DURATION = 200L
        const val ARROW_ROTATE_DEGREE = 180f
    }
}

enum class DialogCategory {
    DELETE,
    RESERVED,
    ALREADY_RESERVED,
    SOLD_OUT,
    ALREADY_SOLD_OUT
}