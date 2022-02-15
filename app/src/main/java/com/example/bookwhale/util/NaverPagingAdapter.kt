package com.example.bookwhale.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.bookwhale.databinding.ViewholderNaverbooklistBinding
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.widget.listener.AdapterListener
import com.example.bookwhale.widget.listener.main.article.NaverBookListener

class NaverPagingAdapter(
    private val adapterListener: AdapterListener
) : PagingDataAdapter<NaverBookModel, NaverPagingViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NaverPagingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return NaverPagingViewHolder(
            ViewholderNaverbooklistBinding.inflate(layoutInflater, parent, false)
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
    private val binding: ViewholderNaverbooklistBinding
): RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(model: NaverBookModel) {
        binding.officialBookNameTextView.text = model.bookTitle.replace("<b>","").replace("</b>","")
        binding.officialBookImageView.load(model.bookThumbnail,4f,CenterCrop())
        binding.officialPriceTextView.text = "${model.bookListPrice.replace("<b>","").replace("</b>","")}원"
        binding.officialPublisherTextView.text = "출판 ${model.bookPublisher.replace("<b>","").replace("</b>","")}"
        binding.officialWriterTextView.text = "글 ${model.bookAuthor.replace("<b>","").replace("</b>","")}"

    }

    fun bindViews(model: NaverBookModel, adapterListener: AdapterListener) {
        if (adapterListener is NaverBookListener) {
            binding.root.setOnClickListener {
                adapterListener.onClickItem(model)
            }
        }
    }
}
