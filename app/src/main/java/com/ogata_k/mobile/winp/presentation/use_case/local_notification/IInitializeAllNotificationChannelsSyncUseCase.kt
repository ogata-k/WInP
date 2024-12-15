package com.ogata_k.mobile.winp.presentation.use_case.local_notification

import com.ogata_k.mobile.winp.domain.component.LocalNotificationScheduler
import com.ogata_k.mobile.winp.domain.use_case.local_notification.InitializeAllNotificationChannelsInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.InitializeAllNotificationChannelsSyncUseCase

class IInitializeAllNotificationChannelsSyncUseCase(private val localNotificationScheduler: LocalNotificationScheduler) :
    InitializeAllNotificationChannelsSyncUseCase {
    override fun call(input: InitializeAllNotificationChannelsInput) {
            localNotificationScheduler.initializeNotificationChannelsForAllLocalNotifyDiv()
    }
}