package com.ogata_k.mobile.winp.domain.use_case.local_notification

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.model.notification.LocalNotification
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase
import java.util.Optional

data class GetLocalNotificationInput(val notifyDiv: LocalNotifyDiv)

typealias GetLocalNotificationOutput = Result<Optional<LocalNotification>>

interface GetLocalNotificationAsyncUseCase :
    AsyncUseCase<GetLocalNotificationInput, GetLocalNotificationOutput>