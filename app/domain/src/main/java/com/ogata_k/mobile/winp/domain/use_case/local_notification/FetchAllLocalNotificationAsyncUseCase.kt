package com.ogata_k.mobile.winp.domain.use_case.local_notification

import com.ogata_k.mobile.winp.domain.model.notification.LocalNotification
import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

object FetchAllLocalNotificationInput

typealias FetchAllLocalNotificationOutput = Result<List<LocalNotification>>

interface FetchAllLocalNotificationAsyncUseCase :
    AsyncUseCase<FetchAllLocalNotificationInput, FetchAllLocalNotificationOutput>