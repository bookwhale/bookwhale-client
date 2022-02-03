package com.example.bookwhale.util

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookwhale.databinding.ViewholderArticlelistBinding
import com.example.bookwhale.model.main.home.ArticleModel

class PagingAdapter : PagingDataAdapter<ArticleModel, PagingViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PagingViewHolder(
            ViewholderArticlelistBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ArticleModel>() {
            override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
                return oldItem == newItem
            }
        } }
}

class PagingViewHolder(
    private val binding: ViewholderArticlelistBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(value: ArticleModel) {
        binding.timeTextView.text = value.beforeTime
        binding.titleTextView.text = value.articleTitle
        binding.locationTextView.text = value.sellingLocation
        binding.qualityTextView.text = value.bookStatus
        binding.chatTextView.text = value.chatCount.toString()
        binding.priceTextView.text = value.articlePrice
        binding.thumbNailImageView.load(value.articleImage.toString())

        if(value.chatCount == 0) binding.chatGroup.isGone = true
        if(value.favoriteCount == 0) binding.heartGroup.isGone = true
    }
}
