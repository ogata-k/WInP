package com.ogata_k.mobile.winp.presentation.event.snackbar.work_comment

import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventKind
import com.ogata_k.mobile.winp.presentation.event.EventTarget
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import java.time.LocalDateTime

/**
 * 成功時に処理を通知するためのEvent
 * Snackbar用のEventを監視しているところで画面のPOPをするために通知するなどを想定している。
 * ほかのようにSucceededCreateなどとしないのはトースト用のEventとの重複除けと操作のたびに作らないといけないことへの対策
 */
data class DoneWorkComment(
    val workCommentId: Long,
    private val action: EventAction,
    private val timestamp: LocalDateTime = LocalDateTime.now()
) : SnackbarEvent {
    override fun getTarget(): EventTarget {
        return EventTarget.WORK_COMMENT
    }

    override fun getKind(): EventKind {
        return EventKind.SUCCEEDED
    }

    override fun getAction(): EventAction {
        return action
    }
}
