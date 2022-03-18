package com.example.bookwhale.widget.viewholder.main.favorite

import android.annotation.SuppressLint
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.databinding.ViewholderArticlelistBinding
import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.favorite.FavoriteListener
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


    @SuppressLint("SetTextI18n")
    override fun bindData(model: FavoriteModel) {
        super.bindData(model)
        with(binding) {
            titleTextView.text = model.articleTitle
            priceTextView.text = "${model.articlePrice}원"
            locationTextView.text = model.sellingLocation
            timeTextView.text = model.beforeTime
            chatTextView.text = model.chatCount.toString()
            heartTextView.text = model.favoriteCount.toString()
            qualityTextView.text = model.bookStatus
            locationTextView.text = model.sellingLocation
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
    }
}