package com.example.bookwhale.widget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.databinding.ViewholderClosedChatBinding
import com.example.bookwhale.databinding.ViewholderMyChatBinding
import com.example.bookwhale.databinding.ViewholderOpponentChatBinding
import com.example.bookwhale.model.MessageType
import com.example.bookwhale.model.main.chat.ChatMessageModel
import com.example.bookwhale.util.load

class ChatPagingAdapter(
    private val senderProfileImage: String?
) : PagingDataAdapter<ChatMessageModel, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            MY_MESSAGE -> MyChatPagingViewHolder(ViewholderMyChatBinding.inflate(layoutInflater, parent, false))
            OPPONENT_MESSAGE -> OpponentChatPagingViewHolder(ViewholderOpponentChatBinding.inflate(layoutInflater, parent, false), senderProfileImage)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = getItem(position)
        when (holder) {
            is MyChatPagingViewHolder -> {
                if (model != null) {
                    holder.bind(model)
                }
            }
            is OpponentChatPagingViewHolder -> {
                if (model != null) {
                    holder.bind(model)
                }
            }

            is ClosedChatViewHolder -> {
                if (model != null) {
                    holder.bind(model)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.type) {
            MessageType.MY -> MY_MESSAGE
            MessageType.OPPONENT -> OPPONENT_MESSAGE
            MessageType.CLOSE -> CLOSED_ROOM
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ChatMessageModel>() {
            override fun areItemsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel): Boolean {
                return oldItem == newItem
            }
        }

        const val MY_MESSAGE = 0
        const val OPPONENT_MESSAGE = 1
        const val CLOSED_ROOM = 2
    }
}

class MyChatPagingViewHolder(
    private val binding: ViewholderMyChatBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ChatMessageModel) {
        binding.myTextView.text = model.content
    }
}

class OpponentChatPagingViewHolder(
    private val binding: ViewholderOpponentChatBinding,
    private val senderProfileImage: String?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ChatMessageModel) {
        binding.opponentTextView.text = model.content
        senderProfileImage?.let {
            binding.opponentProfileImageView.load(it, 16f, CenterCrop())
        }
    }
}

class ClosedChatViewHolder(
    private val binding: ViewholderClosedChatBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ChatMessageModel) {}
}
