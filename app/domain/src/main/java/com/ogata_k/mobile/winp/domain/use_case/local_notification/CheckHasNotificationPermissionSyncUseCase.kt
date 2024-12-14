package com.ogata_k.mobile.winp.domain.use_case.local_notification

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.use_case.SyncUseCase

data class CheckHasNotificationPermissionInput(val notifyDiv: LocalNotifyDiv)

typealias CheckHasNotificationPermissionOutput = Boolean

interface CheckHasNotificationPermissionSyncUseCase :
    SyncUseCase<CheckHasNotificationPermissionInput, CheckHasNotificationPermissionOutput>
