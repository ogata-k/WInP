package com.ogata_k.mobile.winp.presentation.event.snackbar.setting

import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventKind
import com.ogata_k.mobile.winp.presentation.event.EventTarget
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import java.time.LocalDateTime

class FailedUpdateSetting(
    private val timestamp: LocalDateTime = LocalDateTime.now()
) : SnackbarEvent {
    override fun getTarget(): EventTarget {
        return EventTarget.Setting
    }

    override fun getKind(): EventKind {
        return EventKind.FAILED
    }

    override fun getAction(): EventAction {
        return EventAction.UPDATE
    }
}