package com.ogata_k.mobile.winp.presentation.event.toast.work

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventKind
import com.ogata_k.mobile.winp.presentation.event.EventTarget
import com.ogata_k.mobile.winp.presentation.event.toast.ToastEvent

data class NotFoundWork(val workId: Long) : ToastEvent {
    override fun getTarget(): EventTarget {
        return EventTarget.WORK
    }

    override fun getKind(): EventKind {
        return EventKind.FAILED
    }

    override fun getAction(): EventAction {
        return EventAction.OTHER
    }

    @Composable
    override fun toMessage(): String {
        return stringResource(R.string.no_exist_data).format(getTarget().getName())
    }
}
