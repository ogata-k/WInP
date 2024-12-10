package com.ogata_k.mobile.winp.presentation.component

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.ogata_k.mobile.winp.common.type_converter.LocalTimeConverter
import com.ogata_k.mobile.winp.domain.component.AlarmScheduler
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

class IAlarmScheduler(private val context: Context, private val manager: AlarmManager) :
    AlarmScheduler {
    override fun scheduleInexactRepeating(
        requestCode: Int,
        alarmType: Int,
        notifyTime: LocalTime,
        canSkipPastNotifyTime: Boolean,
        intervalMills: Long,
        intentBuilder: (context: Context, requestCode: Int) -> PendingIntent
    ) {
        val nowDateTime: OffsetDateTime = OffsetDateTime.now()
        val targetDateTime: OffsetDateTime = LocalTimeConverter
            .toOffsetTime(notifyTime)
            .atDate(LocalDate.now())
            .let {
                if (canSkipPastNotifyTime && nowDateTime >= it) {
                    // 設定しようとしている日時が過ぎていたので翌日に繰り越し
                    return@let it.plusDays(1)
                }

                return@let it
            }

        manager.setInexactRepeating(
            requestCode,
            targetDateTime.toZonedDateTime().toInstant().toEpochMilli(),
            intervalMills,
            intentBuilder(context, requestCode),
        )
    }

    override fun cancel(
        requestCode: Int,
        intentBuilder: (context: Context, requestCode: Int) -> PendingIntent
    ) {
        manager.cancel(intentBuilder(context, requestCode))
    }
}