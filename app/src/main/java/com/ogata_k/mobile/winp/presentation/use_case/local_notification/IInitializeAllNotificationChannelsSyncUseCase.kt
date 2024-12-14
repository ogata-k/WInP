package com.ogata_k.mobile.winp.presentation.use_case.local_notification

import android.os.Build
import com.ogata_k.mobile.winp.domain.component.LocalNotificationScheduler
import com.ogata_k.mobile.winp.domain.use_case.local_notification.InitializeAllNotificationChannelsInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.InitializeAllNotificationChannelsSyncUseCase

class IInitializeAllNotificationChannelsSyncUseCase(private val localNotificationScheduler: LocalNotificationScheduler) :
    InitializeAllNotificationChannelsSyncUseCase {
    override fun call(input: InitializeAllNotificationChannelsInput) {
        // バージョン１２L以前だとチャネルを作ったときに通知権限がリクエストされる
        // そのため、１３以降のバージョンと通知権限依頼発行の挙動を合わせるために１３以降のみチャネルを作成する
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            localNotificationScheduler.initializeNotificationChannelsForAllLocalNotifyDiv()
        }
    }
}