package com.example.bookwhale.widget.viewholder.main.likelist

import com.example.bookwhale.R
import com.example.bookwhale.databinding.ViewholderArticlelistBinding
import com.example.bookwhale.model.main.likelist.LikeArticleModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.home.ArticleListListener
import com.example.bookwhale.widget.listener.main.likelist.LikeListListener
import com.example.bookwhale.widget.viewholder.ModelViewHolder

class LikeListViewHolder(
    private val binding: ViewholderArticlelistBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
) : ModelViewHolder<LikeArticleModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = Unit

    override fun bindViews(model: LikeArticleModel, adapterListener: AdapterListener) {
        if (adapterListener is LikeListListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
        binding.heartImageView.setImageResource(R.drawable.ic_heart_filled)
    }

    override fun bindData(model: LikeArticleModel) {
        super.bindData(model)
        with(binding) {
            titleTextView.text = model.postResponse.postTitle
            priceTextView.text = model.postResponse.postPrice
            locationTextView.text = model.postResponse.sellingLocation
            timeTextView.text = model.postResponse.beforeTime
            chatTextView.text = model.postResponse.viewCount.toString()
            heartTextView.text = model.postResponse.likeCount.toString()
        }
    }


}