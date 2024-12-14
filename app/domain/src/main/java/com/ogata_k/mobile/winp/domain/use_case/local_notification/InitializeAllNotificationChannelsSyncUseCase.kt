package com.ogata_k.mobile.winp.domain.use_case.local_notification

import com.ogata_k.mobile.winp.domain.use_case.SyncUseCase

typealias InitializeAllNotificationChannelsInput = Unit

typealias InitializeAllNotificationChannelsOutput = Unit

interface InitializeAllNotificationChannelsSyncUseCase :
    SyncUseCase<InitializeAllNotificationChannelsInput, InitializeAllNotificationChannelsOutput>