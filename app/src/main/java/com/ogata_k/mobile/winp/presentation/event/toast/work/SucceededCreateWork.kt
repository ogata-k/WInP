package com.ogata_k.mobile.winp.presentation.event.toast.work

import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventKind
import com.ogata_k.mobile.winp.presentation.event.EventTarget
import com.ogata_k.mobile.winp.presentation.event.toast.ToastEvent


data class SucceededCreateWork(val workId: Long) : ToastEvent {
    override fun getTarget(): EventTarget {
        return EventTarget.WORK
    }

    override fun getKind(): EventKind {
        return EventKind.SUCCEEDED
    }

    override fun getAction(): EventAction {
        return EventAction.CREATE
    }
}

