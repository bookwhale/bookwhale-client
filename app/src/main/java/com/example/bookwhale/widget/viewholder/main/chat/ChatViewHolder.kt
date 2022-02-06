package com.example.bookwhale.widget.viewholder.main.chat

import com.example.bookwhale.databinding.ViewholderArticlelistBinding
import com.example.bookwhale.databinding.ViewholderChatBinding
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.chat.ChatListener
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
import com.example.bookwhale.widget.viewholder.ModelViewHolder


class ChatViewHolder(
    private val binding: ViewholderChatBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
) : ModelViewHolder<ChatModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = Unit

    override fun bindViews(model: ChatModel, adapterListener: AdapterListener) {
        if (adapterListener is ChatListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
    }

    override fun bindData(model: ChatModel) {
        super.bindData(model)
        with(binding) {

        }
    }
}