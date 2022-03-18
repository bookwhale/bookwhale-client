package com.example.bookwhale.widget.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.databinding.ViewholderArticlelistBinding
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.util.load
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.home.ArticleListListener

class PagingAdapter(
    private val adapterListener: AdapterListener
) : PagingDataAdapter<ArticleModel, PagingViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PagingViewHolder(
            ViewholderArticlelistBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        val model = getItem(position)
        if (model != null) {
            holder.bind(model)
            holder.bindViews(model, adapterListener)
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

    @SuppressLint("SetTextI18n")
    fun bind(model: ArticleModel) = with(binding) {
        timeTextView.text = model.beforeTime
        titleTextView.text = model.articleTitle
        locationTextView.text = model.sellingLocation
        qualityTextView.text = model.bookStatus
        chatTextView.text = model.chatCount.toString()
        priceTextView.text = "${model.articlePrice}원"
        heartTextView.text = model.favoriteCount.toString()
        thumbNailImageView.load(model.articleImage.toString(), 4f, CenterCrop())

        if(model.chatCount == 0) {
            chatGroup.isGone = true
        } else {
            chatGroup.isVisible = true
        }
        if(model.favoriteCount == 0) {
            heartGroup.isGone = true
        } else {
            heartGroup.isVisible = true
        }
    }

    fun bindViews(model: ArticleModel, adapterListener: AdapterListener) {
        if (adapterListener is ArticleListListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
    }
}
