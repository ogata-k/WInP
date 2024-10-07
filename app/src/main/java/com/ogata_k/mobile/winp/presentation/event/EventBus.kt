package com.ogata_k.mobile.winp.presentation.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
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

    suspend fun post(event: Event) {
        _eventPublisher.emit(event)
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

    inline fun onAnyEvent(
        lifecycleOwner: LifecycleOwner,
        crossinline onEvent: (Any) -> Unit
    ) {
        lifecycleOwner.lifecycleScope.launch {
            eventSubscriber.collectLatest { event: Any ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
        }
    }
}