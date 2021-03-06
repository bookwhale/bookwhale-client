package com.example.bookwhale.widget.viewholder.article

import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.databinding.ViewholderTempimageBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.DEFAULT_IMAGEVIEW_RADIUS
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.article.PostImageListener
import com.example.bookwhale.widget.viewholder.ModelViewHolder

class TempImageViewHolder(
    private val binding: ViewholderTempimageBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
) : ModelViewHolder<DetailImageModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = Unit

    override fun bindViews(model: DetailImageModel, adapterListener: AdapterListener) {
        if (adapterListener is PostImageListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
            binding.closeLayout.setOnClickListener {
                adapterListener.onDeleteItem(model)
            }
        }
    }

    override fun bindData(model: DetailImageModel) {
        super.bindData(model)
        with(binding) {
            model.articleImage?.let { imageView.load(it, DEFAULT_IMAGEVIEW_RADIUS, CenterCrop()) }
        }
    }
}
