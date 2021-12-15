package com.example.bookwhale.util.mapper

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.bookwhale.databinding.ViewholderArticlelistBinding
import com.example.bookwhale.model.CellType
import com.example.bookwhale.model.Model
import com.example.bookwhale.screen.base.BaseViewModel
import com.example.bookwhale.screen.main.lielist.LikeListViewModel
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.viewholder.ModelViewHolder
import com.example.bookwhale.widget.viewholder.main.home.ArticleListViewHolder
import com.example.bookwhale.widget.viewholder.main.likelist.LikeListViewHolder


object ModelViewHolderMapper {

    @Suppress("UNCHECKED_CAST")
    fun <M : Model> map(
        parent: ViewGroup,
        type: CellType,
        viewModel: BaseViewModel,
        resourcesProvider: ResourcesProvider
    ): ModelViewHolder<M> {
        var inflater = LayoutInflater.from(parent.context)
        val viewHolder = when (type) {

            CellType.ARTICLE_LIST -> ArticleListViewHolder(
                ViewholderArticlelistBinding.inflate(inflater, parent, false),
                viewModel,
                resourcesProvider
            )

            CellType.LIKE_LIST -> LikeListViewHolder(
                ViewholderArticlelistBinding.inflate(inflater, parent, false),
                viewModel,
                resourcesProvider
            )

        }
        return viewHolder as ModelViewHolder<M>
    }
}