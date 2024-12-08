package com.ogata_k.mobile.winp.domain.infra.database.dao

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.model.notification.LocalNotification
import java.time.OffsetTime
import java.util.Optional

/**
 * 通知のDAO
 */
interface LocalNotificationDao {
    suspend fun fetchAll(): List<LocalNotification>

    suspend fun findLocalNotification(notifyDiv: LocalNotifyDiv): Optional<LocalNotification>

    suspend fun upsertLocalNotification(notifyDiv: LocalNotifyDiv, notifyTime: OffsetTime)

    suspend fun deleteLocalNotification(notifyDiv: LocalNotifyDiv)
}