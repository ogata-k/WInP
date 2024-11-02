package com.ogata_k.mobile.winp.presentation.event.toast

import com.ogata_k.mobile.winp.presentation.event.Event
import com.ogata_k.mobile.winp.presentation.event.EventFormat

interface ToastEvent : Event {
    override fun getFormat(): EventFormat {
        return EventFormat.DETAIL
    }
}