package com.example.bookwhale.widget.listener.main

import com.example.bookwhale.model.main.ArticleModel
import com.example.bookwhale.widget.listener.AdapterListener

interface ArticleListListener: AdapterListener {

    fun onClickItem(model: ArticleModel)
}