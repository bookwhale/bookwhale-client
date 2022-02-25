package com.example.bookwhale.screen.chatroom

import androidx.lifecycle.viewModelScope
import com.example.bookwhale.data.repository.chat.ChatRepository
import com.example.bookwhale.screen.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChatRoomViewModel(
    private val chatRepository: ChatRepository
): BaseViewModel() {

    fun load(roomId: Int) = viewModelScope.launch {
        chatRepository.getChatRoomDetail(roomId)
    }
}