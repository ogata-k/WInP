package com.ogata_k.mobile.winp.infra.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.infra.database.entity.LocalNotification
import java.util.Optional

@Dao
interface LocalNotificationDao {
    @Query("SELECT local_notifications.* FROM local_notifications")
    suspend fun fetchAll(): List<LocalNotification>

    @Query(
        """
        SELECT local_notifications.* 
        FROM local_notifications
        WHERE local_notify_div = :notifyDiv
        LIMIT 1
    """
    )
    suspend fun findLocalNotification(notifyDiv: LocalNotifyDiv): Optional<LocalNotification>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalNotification(notification: LocalNotification)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocalNotification(notification: LocalNotification)

    @Query("DELETE FROM local_notifications WHERE local_notify_div = :notifyDiv")
    suspend fun deleteLocalNotification(notifyDiv: LocalNotifyDiv)
}