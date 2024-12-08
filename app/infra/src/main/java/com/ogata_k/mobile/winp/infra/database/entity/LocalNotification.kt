package com.ogata_k.mobile.winp.infra.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.OffsetTime

@Entity(
    tableName = "local_notifications",
    indices = [
        Index(value = ["local_notify_div"], unique = true),
    ],
)
data class LocalNotification(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "local_notification_id")
    val localNotificationId: Long,

    @ColumnInfo(name = "local_notify_div")
    val localNotifyDiv: Int,

    @ColumnInfo(name = "notify_time")
    val notifyTime: OffsetTime,
)
