package com.ogata_k.mobile.winp.presentation.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ogata_k.mobile.winp.presentation.event.toast.ToastEvent
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

/**
 * アプリのPresentation層で監視したいイベントが流れるBus
 */
object EventBus {
    private val _eventPublisher = MutableSharedFlow<Any>()
    val eventSubscriber = _eventPublisher.asSharedFlow()

    private suspend fun postEvent(event: Event) {
        _eventPublisher.emit(event)
    }

    suspend fun postToastEvent(event: ToastEvent) {
        postEvent(event)
    }

    inline fun <reified T : Event> onEvent(
        lifecycleOwner: LifecycleOwner,
        crossinline onEvent: (T) -> Unit
    ) {
        lifecycleOwner.lifecycleScope.launch {
            eventSubscriber.filterIsInstance<T>()
                .collectLatest { event: T ->
                    coroutineContext.ensureActive()
                    onEvent(event)
                }
        }
    }
}