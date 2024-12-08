package com.ogata_k.mobile.winp.domain.use_case.local_notification

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase
import java.time.OffsetTime

data class UpsertLocalNotificationInput(val notifyDiv: LocalNotifyDiv, val notifyTime: OffsetTime)

typealias UpsertLocalNotificationOutput = Result<Unit>

interface UpsertLocalNotificationAsyncUseCase :
    AsyncUseCase<UpsertLocalNotificationInput, UpsertLocalNotificationOutput>