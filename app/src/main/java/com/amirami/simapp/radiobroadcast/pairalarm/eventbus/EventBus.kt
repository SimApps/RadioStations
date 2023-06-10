package com.amirami.simapp.radiobroadcast.pairalarm.eventbus

import kotlinx.coroutines.flow.*

object EventBus {
    private val _events = MutableSharedFlow<Any>()

    val events = _events.asSharedFlow()

    suspend fun post(event: Any) {
        _events.emit(event)
    }

    inline fun <reified T> subscribe(): Flow<T> {
        return events.filter { it is T }.map { it as T }
    }
}
