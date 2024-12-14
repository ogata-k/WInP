package com.ogata_k.mobile.winp.domain.use_case.local_notification

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.use_case.SyncUseCase

data class RequestNotificationPermissionInput(val notifyDiv: LocalNotifyDiv)

typealias RequestNotificationPermissionOutput = Unit

interface RequestNotificationPermissionSyncUseCase :
    SyncUseCase<RequestNotificationPermissionInput, RequestNotificationPermissionOutput>