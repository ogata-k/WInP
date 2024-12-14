package com.ogata_k.mobile.winp.presentation.use_case.local_notification

import com.ogata_k.mobile.winp.domain.component.LocalNotificationScheduler
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RequestNotificationPermissionInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RequestNotificationPermissionSyncUseCase

class IRequestNotificationPermissionSyncUseCase(private val localNotificationScheduler: LocalNotificationScheduler) :
    RequestNotificationPermissionSyncUseCase {
    override fun call(input: RequestNotificationPermissionInput) {
        localNotificationScheduler.requestNotificationPermission(input.notifyDiv)
    }
}