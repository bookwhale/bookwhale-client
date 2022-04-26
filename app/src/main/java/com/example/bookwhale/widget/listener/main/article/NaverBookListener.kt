package com.example.bookwhale.widget.listener.main.article

import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.widget.listener.AdapterListener

interface NaverBookListener : AdapterListener {

    fun onClickItem(model: NaverBookModel)
}
