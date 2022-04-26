package com.example.bookwhale.widget.viewholder.main.chat

import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ViewholderChatBinding
import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.chat.ChatListener
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
            profileTextView.text = model.opponentIdentity
            articleNameTextView.text = model.articleTitle
            model.articleImage?.let {
                articleImageView.load(it, 4f, CenterCrop())
            } ?: run {
                articleImageView.setImageResource(R.drawable.ic_logo)
            }
            if (model.lastContent.isNullOrEmpty()) {
                lastChatTextView.text = resourcesProvider.getString(R.string.noChatMessage)
            } else {
                lastChatTextView.text = model.lastContent
            }
        }
    }
}
