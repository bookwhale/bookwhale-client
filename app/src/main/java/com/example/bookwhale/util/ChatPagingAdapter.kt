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
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.article.NaverBookListener


class ChatPagingAdapter(
    private val adapterListener: AdapterListener
) : PagingDataAdapter<ChatMessageModel, RecyclerView.ViewHolder>(diffCallback) {

    private val ITEM_VIEW_TYPE_IMAGE = 0
    private val ITEM_VIEW_TYPE_TEXT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            ITEM_VIEW_TYPE_IMAGE -> MyChatPagingViewHolder(ViewholderMyChatBinding.inflate(layoutInflater, parent, false))
            ITEM_VIEW_TYPE_TEXT -> OpponentChatPagingViewHolder(ViewholderOpponentChatBinding.inflate(layoutInflater, parent, false))
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
        return super.getItemViewType(position)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ChatMessageModel>() {
            override fun areItemsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatMessageModel, newItem: ChatMessageModel): Boolean {
                return oldItem == newItem
            }
        } }
}

class MyChatPagingViewHolder(
    private val binding: ViewholderMyChatBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ChatMessageModel) {

    }
}

class OpponentChatPagingViewHolder(
    private val binding: ViewholderOpponentChatBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: ChatMessageModel) {

    }
}

