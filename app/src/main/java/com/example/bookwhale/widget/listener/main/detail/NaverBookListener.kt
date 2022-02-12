package com.example.bookwhale.widget.listener.main.detail

import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.widget.listener.AdapterListener

interface NaverBookListener: AdapterListener {

    fun onClickItem(model: NaverBookModel)

}