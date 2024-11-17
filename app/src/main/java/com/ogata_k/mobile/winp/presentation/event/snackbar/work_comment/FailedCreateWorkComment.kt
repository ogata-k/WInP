package com.ogata_k.mobile.winp.presentation.event.snackbar.work_comment

import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventKind
import com.ogata_k.mobile.winp.presentation.event.EventTarget
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import java.time.LocalDateTime

data class FailedCreateWorkComment(
    val workCommentId: Long,
    private val timestamp: LocalDateTime = LocalDateTime.now()
) : SnackbarEvent {
    override fun getTarget(): EventTarget {
        return EventTarget.WORK_COMMENT
    }

    override fun getKind(): EventKind {
        return EventKind.FAILED
    }

    override fun getAction(): EventAction {
        return EventAction.CREATE
    }
}
