package com.ogata_k.mobile.winp.presentation.event.toast.common

import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventKind
import com.ogata_k.mobile.winp.presentation.event.EventTarget
import com.ogata_k.mobile.winp.presentation.event.toast.ToastEvent
import java.time.LocalDateTime

data class ErrorOccurred(private val timestamp: LocalDateTime = LocalDateTime.now()) : ToastEvent {
    override fun getTarget(): EventTarget {
        return EventTarget.ERROR
    }

    override fun getKind(): EventKind {
        return EventKind.FAILED
    }

    override fun getAction(): EventAction {
        return EventAction.OTHER
    }
}
