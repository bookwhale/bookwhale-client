package com.example.bookwhale.widget.viewholder.main.favorite

import com.example.bookwhale.databinding.ViewholderArticlelistBinding
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
import com.example.bookwhale.widget.listener.main.home.ArticleListListener
import com.example.bookwhale.widget.viewholder.ModelViewHolder

class FavoriteViewHolder(
    private val binding: ViewholderArticlelistBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
) : ModelViewHolder<FavoriteModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = Unit

    override fun bindViews(model: FavoriteModel, adapterListener: AdapterListener) {
        if (adapterListener is FavoriteListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }

            binding.heartImageView.setOnClickListener {
                adapterListener.onClickHeart(model)

            }
        }
    }

    override fun bindData(model: FavoriteModel) {
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