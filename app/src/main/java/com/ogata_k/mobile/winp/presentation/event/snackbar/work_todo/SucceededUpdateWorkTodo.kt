package com.ogata_k.mobile.winp.presentation.event.snackbar.work_todo

import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventKind
import com.ogata_k.mobile.winp.presentation.event.EventTarget
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import java.time.LocalDateTime

data class SucceededUpdateWorkTodo(
    val workId: Long,
    val workTodoId: Long,
    private val timestamp: LocalDateTime = LocalDateTime.now()
) : SnackbarEvent {
    override fun getTarget(): EventTarget {
        return EventTarget.WORK_TODO
    }

    override fun getKind(): EventKind {
        return EventKind.SUCCEEDED
    }

    override fun getAction(): EventAction {
        return EventAction.UPDATE
    }
}
