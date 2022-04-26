package com.example.bookwhale.screen.main.chat

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.bookwhale.databinding.FragmentChatBinding
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.screen.chatroom.ChatRoomActivity
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.chat.ChatListener
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ChatFragment : BaseFragment<MainViewModel, FragmentChatBinding>() {
    override val viewModel by activityViewModels<MainViewModel>()

    override fun getViewBinding(): FragmentChatBinding = FragmentChatBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<ChatModel, MainViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : ChatListener {
                override fun onClickItem(model: ChatModel) {
                    startActivity(ChatRoomActivity.newIntent(requireContext(), model.roomId.toString()))
                }
            }
        )
    }

    override fun initViews() = with(binding) {

        viewModel.loadChatList()

        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadChatList()
    }

    override fun observeData() {
        viewModel.chatStateLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ChatState.Uninitialized -> Unit
                is ChatState.Loading -> handleLoading()
                is ChatState.Success -> handleSuccess(it)
                is ChatState.Error -> handleError(it)
            }
        }
    }

    private fun handleLoading() {
        binding.progressBar.isVisible = true
    }

    private fun handleSuccess(state: ChatState.Success) {
        binding.progressBar.isGone = true

        adapter.submitList(state.chatList)
        if (state.chatList.isNotEmpty()) binding.noChatTextView.isGone = true
    }

    private fun handleError(state: ChatState.Error) {
        binding.progressBar.isGone = true
        when (state.code!!) {
            "T_004" -> handleT004() // AccessToken 만료 코드
        }
    }

    private fun handleT004() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getNewTokens().join()
            viewModel.fetchData()
        }
    }

    companion object {

        fun newInstance() = ChatFragment()

        const val TAG = "ChatFragment"
    }
}
