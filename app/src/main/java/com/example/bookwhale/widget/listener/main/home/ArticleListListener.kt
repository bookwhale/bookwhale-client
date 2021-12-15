package com.example.bookwhale.widget.listener.main.home

import com.example.bookwhale.model.main.home.ArticleModel
import com.example.bookwhale.model.main.likelist.LikeArticleModel
import com.example.bookwhale.widget.listener.AdapterListener

interface ArticleListListener: AdapterListener {

    fun onClickItem(model: ArticleModel)

}