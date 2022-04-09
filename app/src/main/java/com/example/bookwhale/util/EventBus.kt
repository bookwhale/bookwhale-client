package com.example.bookwhale.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

class EventBus {
    private val events = MutableSharedFlow<Events>()

    suspend fun produceEvent(event: Events) {
        events.emit(event)
    }

    suspend fun subscribeEvent(event: Events, onEvent: () -> Unit) {
        events.filter { it == event }.collect { onEvent() }
    }
}

enum class Events {
    UploadPostEvent,
    Reserved,
    Sold,
    Deleted,
    Sale,
    DeleteFail
}