package com.example.bookwhale.widget.listener.main.favorite

import com.example.bookwhale.model.main.favorite.FavoriteModel
import com.example.bookwhale.widget.listener.AdapterListener

interface FavoriteListener : AdapterListener {

    fun onClickItem(model: FavoriteModel)
}
