package com.example.bookwhale.screen.chatroom

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ActivityChatRoomBinding
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.load
import com.example.bookwhale.widget.adapter.ChatPagingAdapter
import gun0912.tedimagepicker.util.ToastUtil.context
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class ChatRoomActivity : BaseActivity<ChatRoomViewModel, ActivityChatRoomBinding>() {

    override val viewModel by viewModel<ChatRoomViewModel>()

    override fun getViewBinding(): ActivityChatRoomBinding = ActivityChatRoomBinding.inflate(layoutInflater)

    private val roomId by lazy { intent.getStringExtra(CHATROOM_ID) }
    private lateinit var chatModel : ChatModel

    private val adapter by lazy {
        ChatPagingAdapter(chatModel.opponentProfile)
    }

    override fun initViews() {

        roomId?.let {
            lifecycleScope.launch {
                val result = async { viewModel.loadChatModel(it.toInt()) }

                chatModel = result.await()

                viewModel.runStomp(it.toInt(), getMessageText())

                binding.recyclerView.adapter = adapter

                chatModel.let { data ->
                    binding.articleTitleTextView.text = data.articleTitle
                    data.articleImage?.let {
                        binding.articleImageView.load(it, 4f, CenterCrop())
                    }
                }

                getMessages()
                initButtons()

                adapter.addLoadStateListener { loadState ->
                    if (loadState.source.refresh is LoadState.Loading) {
                        binding.progressBar.isVisible = true
                    } else {
                        binding.progressBar.isGone = true
                        binding.recyclerView.scrollToPosition(0)
                    }
                }
            }
        }



    }

    private fun initButtons() = with(binding) {
        sendButton.setOnClickListener {
            roomId?.let {
                viewModel.sendMessage(it.toInt(), getMessageText())
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }


    private fun getMessages() = with(binding) {
        roomId?.let {
            lifecycleScope.launch {
                viewModel.getPreviousMessages(it.toInt()).collectLatest {
                    adapter.submitData(it)
                }
                Log.e("getMessage2", adapter.itemCount.toString())
            }
        }
    }

    private fun getMessageText() : String = with(binding) {
        return editText.text.toString()
    }

    companion object {

        fun newIntent(context: Context, roomId: String) = Intent(context, ChatRoomActivity::class.java).apply {
            putExtra(CHATROOM_ID, roomId)
        }

        const val CHATROOM_ID = "roomId"
    }

    override fun observeData() {
        viewModel.chatRoomState.observe(this) {
            when(it) {
                is ChatRoomState.Uninitialized -> Unit
                is ChatRoomState.Loading -> handleLoading()
                is ChatRoomState.Success -> handleSuccess()
                is ChatRoomState.Error -> handleError(it)
            }
        }
        viewModel.socketState.observe(this) {
            when(it) {
                is SocketState.Uninitialized -> Unit
                is SocketState.Loading -> Unit
                is SocketState.MsgReceived -> getMessages()
                is SocketState.MsgSend -> binding.editText.text.clear()
                is SocketState.Error -> handleMsgError(it)
            }
        }
    }

    private fun handleMsgError(state: SocketState.Error) {
        Log.e("MsgError",state.code.toString())
    }

    private fun handleLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleSuccess() {
        binding.progressBar.isGone = true
    }

    private fun handleError(state: ChatRoomState.Error) {
        binding.progressBar.isGone = true
        when(state.code!!) {
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
            initViews()
        }
    }

}