package com.example.bookwhale.screen.main.chat

import android.util.Log
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.bookwhale.databinding.FragmentChatBinding
import com.example.bookwhale.databinding.FragmentFavoriteBinding
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseFragment
import com.example.bookwhale.screen.main.MainViewModel
import com.example.bookwhale.screen.main.favorite.FavoriteFragment
import com.example.bookwhale.screen.main.favorite.FavoriteState
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.main.chat.ChatListener
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ChatFragment: BaseFragment<ChatViewModel, FragmentChatBinding>() {
    override val viewModel by viewModel<ChatViewModel>()

    override fun getViewBinding(): FragmentChatBinding = FragmentChatBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<ChatModel, ChatViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : ChatListener {
                override fun onClickItem(model: ChatModel) {
                    //
                }
            }
        )
    }

    override fun initViews() = with(binding) {
        recyclerView.adapter = adapter
    }

    override fun observeData()  {
        viewModel.chatStateLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is ChatState.Uninitialized -> Unit
                is ChatState.Loading -> handleLoading()
                is ChatState.Success -> handleSuccess(it)
                is ChatState.Error -> handleError()
            }
        }
    }

    private fun handleLoading() {
        //
    }

    private fun handleSuccess(state: ChatState.Success) {
        Log.e("state?",state.chatList.toString())
        adapter.submitList(state.chatList)

        if(state.chatList.isNotEmpty()) binding.noChatTextView.isGone = true
    }

    private fun handleError() {
//
    }

    companion object {

        fun newInstance() = ChatFragment()

        const val TAG = "ChatFragment"
    }
}
