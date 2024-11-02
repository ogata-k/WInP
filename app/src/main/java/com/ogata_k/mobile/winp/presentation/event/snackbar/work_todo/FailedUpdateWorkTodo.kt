package com.ogata_k.mobile.winp.presentation.event.snackbar.work_todo

import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventKind
import com.ogata_k.mobile.winp.presentation.event.EventTarget
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent

data class FailedUpdateWorkTodo(val workId: Long, val workTodoId: Long?) : SnackbarEvent {
    override fun getTarget(): EventTarget {
        return EventTarget.WORK_TODO
    }

    override fun getKind(): EventKind {
        return EventKind.FAILED
    }

    override fun getAction(): EventAction {
        return EventAction.UPDATE
    }
}
