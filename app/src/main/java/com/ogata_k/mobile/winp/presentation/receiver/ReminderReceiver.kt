package com.ogata_k.mobile.winp.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.ogata_k.mobile.winp.domain.component.NotificationScheduler
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint(BroadcastReceiver::class)
class ReminderReceiver : Hilt_ReminderReceiver() {
    companion object {
        const val ACTION_REMINDER_NOTIFICATION_ACTION =
            "com.ogata_k.mobile.winp.ACTION_REMINDER_NOTIFICATION"

        // LocalNotifyDivを表す値としてIntの値が指定される
        const val INPUT_LOCAL_NOTIFY_DIV = "ReminderReceiver_LocalNotifyDiv"
    }

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action != ACTION_REMINDER_NOTIFICATION_ACTION) {
            // 想定していない呼び出し
            return
        }

        val localNotifyDivValue = intent.getIntExtra(INPUT_LOCAL_NOTIFY_DIV, -1)
        if (localNotifyDivValue < 0) {
            throw IllegalArgumentException()
        }

        // @todo 実際の通知処理に置き換える
        val message =
            "テスト通知です for %s".format(LocalNotifyDiv.lookup(localNotifyDivValue).toString())
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}