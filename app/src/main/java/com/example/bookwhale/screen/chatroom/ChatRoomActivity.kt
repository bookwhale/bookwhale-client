package com.example.bookwhale.screen.chatroom

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.databinding.ActivityChatRoomBinding
import com.example.bookwhale.databinding.ActivityDetailArticleBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.model.main.chat.ChatMessageModel
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.article.DetailArticleActivity
import com.example.bookwhale.screen.article.DetailArticleViewModel
import com.example.bookwhale.screen.article.PostArticleActivity
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.ChatPagingAdapter
import com.example.bookwhale.util.PagingAdapter
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.home.ArticleListListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ChatRoomActivity : BaseActivity<ChatRoomViewModel, ActivityChatRoomBinding>() {

    override val viewModel by viewModel<ChatRoomViewModel>()

    override fun getViewBinding(): ActivityChatRoomBinding = ActivityChatRoomBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val chatModel by lazy { intent.getParcelableExtra<ChatModel>(CHAT_MODEL) }

    private val adapter by lazy {
        ChatPagingAdapter(
            adapterListener = object : AdapterListener {
            }
        )
    }

    override fun initViews() {
        Log.e("chatModel",chatModel.toString())

        binding.recyclerView.adapter = adapter

        chatModel?.let {
            lifecycleScope.launch {
                viewModel.getPreviousMessages(it.roomId).collectLatest {
                    adapter.submitData(it)
                }
            }
        }

        initButtons()

    }

    private fun initButtons() = with(binding) {
        sendButton.setOnClickListener {
            putMessage()
        }
    }

    private fun getMessageText() : String = with(binding) {
        return editText.text.toString()
    }

    private fun putMessage() {
        viewModel.runStomp(chatModel!!.roomId, getMessageText())
    }

    override fun observeData() {
        //
    }

    private fun initChatRoom() = with(binding) {

    }

    companion object {

        fun newIntent(context: Context, chatModel: ChatModel) = Intent(context, ChatRoomActivity::class.java).apply {
            putExtra(CHAT_MODEL, chatModel)
        }

        const val CHATROOM_ID = "0"
        const val CHAT_MODEL = "chatModel"
    }
}