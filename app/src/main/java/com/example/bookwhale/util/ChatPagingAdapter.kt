package com.example.bookwhale.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.databinding.ViewholderMyChatBinding
import com.example.bookwhale.databinding.ViewholderNaverbooklistBinding
import com.example.bookwhale.databinding.ViewholderOpponentChatBinding
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.model.main.chat.ChatMessageModel
import com.example.bookwhale.model.main.chat.MessageType
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.article.NaverBookListener


class ChatPagingAdapter(
    private val adapterListener: AdapterListener
) : PagingDataAdapter<ChatMessageModel, RecyclerView.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            MY_MESSAGE -> MyChatPagingViewHolder(ViewholderMyChatBinding.inflate(layoutInflater, parent, false))
            OPPONENT_MESSAGE -> OpponentChatPagingViewHolder(ViewholderOpponentChatBinding.inflate(layoutInflater, parent, false))
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
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.type) {
            MessageType.MY -> MY_MESSAGE
            MessageType.OPPONENT -> OPPONENT_MESSAGE
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
    }
}

class MyChatPagingViewHolder(
    private val binding: ViewholderMyChatBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ChatMessageModel) {
        binding.myTextView.text = model.content
    }
}

class OpponentChatPagingViewHolder(
    private val binding: ViewholderOpponentChatBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ChatMessageModel) {
        binding.opponentTextView.text = model.content
    }
}

