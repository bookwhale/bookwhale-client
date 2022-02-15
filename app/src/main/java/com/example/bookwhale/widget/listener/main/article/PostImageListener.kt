package com.example.bookwhale.widget.listener.main.article

import android.net.Uri
import com.example.bookwhale.model.article.DetailImageModel
import com.example.bookwhale.model.article.NaverBookModel
import com.example.bookwhale.widget.listener.AdapterListener

interface PostImageListener: AdapterListener {

    fun onClickItem(model: DetailImageModel)

    fun onDeleteItem(model: DetailImageModel)

}