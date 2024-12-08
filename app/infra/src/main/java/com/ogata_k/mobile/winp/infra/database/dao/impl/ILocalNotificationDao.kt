package com.ogata_k.mobile.winp.infra.database.dao.impl

import androidx.room.withTransaction
import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.infra.database.AppDatabase
import com.ogata_k.mobile.winp.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.infra.database.entity.LocalNotification
import java.time.OffsetTime
import java.util.Optional
import com.ogata_k.mobile.winp.domain.infra.database.dao.LocalNotificationDao as DomainLocalNotificationDao
import com.ogata_k.mobile.winp.domain.model.notification.LocalNotification as DomainLocalNotification

class ILocalNotificationDao(private val db: AppDatabase, private val dao: LocalNotificationDao) :
    DomainLocalNotificationDao {
    override suspend fun fetchAll(): List<DomainLocalNotification> {
        return dao.fetchAll().map { toDomainLocalNotification(it) }
    }

    override suspend fun findLocalNotification(notifyDiv: LocalNotifyDiv): Optional<DomainLocalNotification> {
        return dao.findLocalNotification(notifyDiv).map { toDomainLocalNotification(it) }
    }

    override suspend fun upsertLocalNotification(
        notifyDiv: LocalNotifyDiv,
        notifyTime: OffsetTime
    ) {
        db.withTransaction {
            val stored = dao.findLocalNotification(notifyDiv)
            if (stored.isEmpty) {
                val willStore = LocalNotification(
                    localNotificationId = AsCreate.CREATING_ID,
                    localNotifyDiv = notifyDiv.value,
                    notifyTime = notifyTime,
                )
                dao.insertLocalNotification(willStore)
            } else {
                val willStore: LocalNotification = stored.get().copy(notifyTime = notifyTime)
                dao.updateLocalNotification(willStore)
            }
        }
    }

    override suspend fun deleteLocalNotification(notifyDiv: LocalNotifyDiv) {
        db.withTransaction {
            dao.deleteLocalNotification(notifyDiv)
        }
    }
}

private fun toDomainLocalNotification(localNotification: LocalNotification): DomainLocalNotification {
    return DomainLocalNotification(
        localNotificationId = localNotification.localNotificationId,
        localNotifyDiv = LocalNotifyDiv.lookup(localNotification.localNotifyDiv),
        notifyTime = localNotification.notifyTime
    )
}