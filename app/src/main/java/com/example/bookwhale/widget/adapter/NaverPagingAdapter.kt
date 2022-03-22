package com.example.bookwhale.widget.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.R
import com.example.bookwhale.databinding.ViewholderNaverbooklistBinding
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.util.load
import com.example.bookwhale.util.provider.ResourcesProvider
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.article.NaverBookListener

class NaverPagingAdapter(
    private val resourcesProvider: ResourcesProvider,
    private val adapterListener: AdapterListener
) : PagingDataAdapter<NaverBookModel, NaverPagingViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NaverPagingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NaverPagingViewHolder(
            ViewholderNaverbooklistBinding.inflate(layoutInflater, parent, false),
            resourcesProvider
        )
    }

    override fun onBindViewHolder(holder: NaverPagingViewHolder, position: Int) {
        val model = getItem(position)
        if (model != null) {
            holder.bind(model)
            holder.bindViews(model, adapterListener)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<NaverBookModel>() {
            override fun areItemsTheSame(oldItem: NaverBookModel, newItem: NaverBookModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: NaverBookModel, newItem: NaverBookModel): Boolean {
                return oldItem == newItem
            }
        } }
}

class NaverPagingViewHolder(
    private val binding: ViewholderNaverbooklistBinding,
    private val resourcesProvider: ResourcesProvider
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: NaverBookModel) {
        binding.officialBookNameTextView.text = model.bookTitle.replace("<b>","").replace("</b>","")
        binding.officialBookImageView.load(model.bookThumbnail,4f,CenterCrop())
        binding.officialPriceTextView.text = resourcesProvider.getString(R.string.price, model.bookListPrice.replace("<b>","").replace("</b>",""))
        binding.officialPublisherTextView.text = resourcesProvider.getString(R.string.publisher, model.bookPublisher.replace("<b>","").replace("</b>",""))
        binding.officialWriterTextView.text = resourcesProvider.getString(R.string.writer, model.bookAuthor.replace("<b>","").replace("</b>",""))

    }

    fun bindViews(model: NaverBookModel, adapterListener: AdapterListener) {
        if (adapterListener is NaverBookListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
    }
}
