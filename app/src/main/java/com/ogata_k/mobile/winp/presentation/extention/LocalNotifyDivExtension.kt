package com.ogata_k.mobile.winp.presentation.extention

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ogata_k.mobile.winp.domain.component.AlarmScheduler
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.presentation.receiver.ReminderReceiver
import java.time.LocalTime

/**
 * アラームをスケジュールする
 */
fun LocalNotifyDiv.scheduleReminder(
    alarmScheduler: AlarmScheduler,
    notifyTime: LocalTime,
    requestCode: Int = this.value,
    canSkipPastNotifyTime: Boolean = true
) {
    val notifyDiv = this
    alarmScheduler.scheduleInexactRepeating(
        requestCode = requestCode,
        alarmType = AlarmManager.RTC_WAKEUP,
        notifyTime = notifyTime,
        canSkipPastNotifyTime = canSkipPastNotifyTime,
        intervalMills = AlarmManager.INTERVAL_DAY,
        intentBuilder = { context, code ->
            notifyDiv.toBroadcastForReminderIntent(
                context,
                code
            )
        }
    )
}

fun LocalNotifyDiv.toBroadcastForReminderIntent(context: Context, requestCode: Int): PendingIntent {
    val localNotifyDivValue: Int = this.value
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        action = ReminderReceiver.ACTION_REMINDER_NOTIFICATION_ACTION
        putExtra(ReminderReceiver.INPUT_LOCAL_NOTIFY_DIV, localNotifyDivValue)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    return pendingIntent
}