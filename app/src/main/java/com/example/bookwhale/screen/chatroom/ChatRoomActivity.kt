package com.example.bookwhale.screen.chatroom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookwhale.databinding.ActivityChatRoomBinding
import com.example.bookwhale.databinding.ActivityDetailArticleBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.screen.article.DetailArticleActivity
import com.example.bookwhale.screen.article.DetailArticleViewModel
import com.example.bookwhale.screen.base.BaseActivity
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.adapter.ModelRecyclerAdapter
import com.example.bookwhale.widget.listener.AdapterListener
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class ChatRoomActivity : BaseActivity<ChatRoomViewModel, ActivityChatRoomBinding>() {

    override val viewModel by viewModel<ChatRoomViewModel>()

    override fun getViewBinding(): ActivityChatRoomBinding = ActivityChatRoomBinding.inflate(layoutInflater)

    private val resourcesProvider by inject<ResourcesProvider>()

    private val adapter by lazy {
        ModelRecyclerAdapter<DetailImageModel, ChatRoomViewModel>(
            listOf(),
            viewModel,
            resourcesProvider,
            adapterListener = object : AdapterListener {
            }
        )
    }

    override fun observeData() {
        //
    }
}