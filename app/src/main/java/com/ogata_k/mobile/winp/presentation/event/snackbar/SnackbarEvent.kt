package com.ogata_k.mobile.winp.presentation.event.snackbar

import com.ogata_k.mobile.winp.presentation.event.Event
import com.ogata_k.mobile.winp.presentation.event.EventFormat

interface SnackbarEvent : Event {
    override fun getFormat(): EventFormat {
        return EventFormat.SIMPLE
    }
}