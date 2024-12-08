package com.ogata_k.mobile.winp.domain.model.notification

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import java.time.OffsetTime

/**
 * ローカルな通知設定
 */
data class LocalNotification(
    val localNotificationId: Long,
    val localNotifyDiv: LocalNotifyDiv,
    val notifyTime: OffsetTime,
)
