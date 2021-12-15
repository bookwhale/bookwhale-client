package com.example.bookwhale.widget.listener.main.likelist

import com.example.bookwhale.model.main.likelist.LikeArticleModel
import com.example.bookwhale.widget.listener.AdapterListener

interface LikeListListener: AdapterListener {

    fun onClickItem(model: LikeArticleModel)

}