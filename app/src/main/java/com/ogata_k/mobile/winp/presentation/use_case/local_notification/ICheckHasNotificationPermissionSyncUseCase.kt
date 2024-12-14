package com.ogata_k.mobile.winp.presentation.use_case.local_notification

import com.ogata_k.mobile.winp.domain.component.LocalNotificationScheduler
import com.ogata_k.mobile.winp.domain.use_case.local_notification.CheckHasNotificationPermissionInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.CheckHasNotificationPermissionOutput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.CheckHasNotificationPermissionSyncUseCase

class ICheckHasNotificationPermissionSyncUseCase(private val localNotificationScheduler: LocalNotificationScheduler) :
    CheckHasNotificationPermissionSyncUseCase {
    override fun call(input: CheckHasNotificationPermissionInput): CheckHasNotificationPermissionOutput {
        return localNotificationScheduler.checkHasNotificationPermission(input.notifyDiv)
    }
}