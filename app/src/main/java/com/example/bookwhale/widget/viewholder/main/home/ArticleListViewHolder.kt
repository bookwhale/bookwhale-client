package com.example.bookwhale.widget.viewholder.main.home

import com.example.bookwhale.databinding.ViewholderArticlelistBinding
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.home.ArticleListListener
import com.example.bookwhale.widget.viewholder.ModelViewHolder


class ArticleListViewHolder(
    private val binding: ViewholderArticlelistBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
) : ModelViewHolder<ArticleModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = Unit

    override fun bindViews(model: ArticleModel, adapterListener: AdapterListener) {
        if (adapterListener is ArticleListListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
            binding.heartImageView.setOnClickListener {
                adapterListener.onClickHeart(model)
            }
        }
    }

    override fun bindData(model: ArticleModel) {
        super.bindData(model)
        with(binding) {
            titleTextView.text = model.articleTitle
            priceTextView.text = model.articlePrice
            locationTextView.text = model.sellingLocation
            timeTextView.text = model.beforeTime
            chatTextView.text = model.chatCount.toString()
            heartTextView.text = model.favoriteCount.toString()
            qualityTextView.text = model.bookStatus
            locationTextView.text = model.sellingLocation
        }
    }
}