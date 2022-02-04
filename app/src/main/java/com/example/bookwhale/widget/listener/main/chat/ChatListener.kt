package com.example.bookwhale.widget.listener.main.chat

import com.example.bookwhale.model.main.chat.ChatModel
import com.example.bookwhale.widget.listener.AdapterListener

interface ChatListener: AdapterListener {

    fun onClickItem(model: ChatModel)

}