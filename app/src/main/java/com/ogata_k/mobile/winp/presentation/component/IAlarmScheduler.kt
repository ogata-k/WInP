package com.ogata_k.mobile.winp.presentation.component

import android.app.AlarmManager
import android.content.Context
import com.ogata_k.mobile.winp.common.type_converter.LocalTimeConverter
import com.ogata_k.mobile.winp.domain.component.AlarmScheduler
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.presentation.constant.WInPRequestCodeCategory
import com.ogata_k.mobile.winp.presentation.extention.toBroadcastForReminderIntent
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

class IAlarmScheduler(private val context: Context, private val manager: AlarmManager) :
    AlarmScheduler {
    override fun scheduleLocalNotifyInexactRepeating(
        notifyDiv: LocalNotifyDiv,
        notifyTime: LocalTime,
    ) {
        val nowDateTime: OffsetDateTime = OffsetDateTime.now()
        val targetDateTime: OffsetDateTime = LocalTimeConverter
            .toOffsetTime(notifyTime)
            .atDate(LocalDate.now())
            .let {
                if (nowDateTime >= it) {
                    // 設定しようとしている日時が過ぎていたので翌日に繰り越し
                    return@let it.plusDays(1)
                }

                return@let it
            }

        val requestCode = WInPRequestCodeCategory.toAlarmLocalNotificationRequestCode(notifyDiv)
        manager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            targetDateTime.toZonedDateTime().toInstant().toEpochMilli(),
            AlarmManager.INTERVAL_DAY,
            notifyDiv.toBroadcastForReminderIntent(
                context,
                requestCode
            ),
        )
    }

    override fun cancel(notifyDiv: LocalNotifyDiv) {
        val requestCode = WInPRequestCodeCategory.toAlarmLocalNotificationRequestCode(notifyDiv)
        manager.cancel(
            notifyDiv.toBroadcastForReminderIntent(
                context,
                requestCode
            ),
        )
    }
}