package com.ogata_k.mobile.winp.domain.use_case.local_notification

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

data class DeleteLocalNotificationInput(val notifyDiv: LocalNotifyDiv)

typealias DeleteLocalNotificationOutput = Result<Unit>

interface DeleteLocalNotificationAsyncUseCase :
    AsyncUseCase<DeleteLocalNotificationInput, DeleteLocalNotificationOutput>