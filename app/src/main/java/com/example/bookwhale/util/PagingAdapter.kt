package com.example.bookwhale.util

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bookwhale.databinding.ViewholderArticlelistBinding
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
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
    fun bind(model: ArticleModel) {
        binding.timeTextView.text = model.beforeTime
        binding.titleTextView.text = model.articleTitle
        binding.locationTextView.text = model.sellingLocation
        binding.qualityTextView.text = model.bookStatus
        binding.chatTextView.text = model.chatCount.toString()
        binding.priceTextView.text = "${model.articlePrice}원"
        binding.heartTextView.text = model.favoriteCount.toString()
        binding.thumbNailImageView.load(model.articleImage.toString())

        if(model.chatCount == 0) binding.chatGroup.isGone = true
        if(model.favoriteCount == 0) binding.heartGroup.isGone = true
    }

    fun bindViews(model: ArticleModel, adapterListener: AdapterListener) {
        if (adapterListener is ArticleListListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
    }
}
