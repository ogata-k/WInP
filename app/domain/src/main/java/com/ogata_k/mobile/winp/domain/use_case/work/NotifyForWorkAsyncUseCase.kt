package com.ogata_k.mobile.winp.domain.use_case.work

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class NotifyForWorkInput(val notifyDiv: LocalNotifyDiv)

typealias NotifyForWorkOutput = Result<Unit>

interface NotifyForWorkAsyncUseCase : AsyncUseCase<NotifyForWorkInput, NotifyForWorkOutput>