package com.example.bookwhale.widget.viewholder.article

import com.example.bookwhale.databinding.ViewholderImageBinding
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.viewholder.ModelViewHolder

class DetailImageViewHolder(
    private val binding: ViewholderImageBinding,
    viewModel: BaseViewModel,
    resourcesProvider: ResourcesProvider
) : ModelViewHolder<DetailImageModel>(binding, viewModel, resourcesProvider) {

    override fun reset() = Unit

    override fun bindViews(model: DetailImageModel, adapterListener: AdapterListener) = Unit

    override fun bindData(model: DetailImageModel) {
        super.bindData(model)
        with(binding) {
            model.articleImage?.let { imageView.load(it) }
        }
    }
}
