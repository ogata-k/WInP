package com.ogata_k.mobile.winp.presentation.component

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.domain.component.LocalNotificationScheduler
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.presentation.activity.MainActivity
import com.ogata_k.mobile.winp.presentation.constant.WInPRequestCodeCategory
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexRouting
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ILocalNotificationScheduler(
    private val context: Context,
    private val manager: NotificationManager,
) : LocalNotificationScheduler {
    companion object {
        private const val WORK_CHANNEL_GROUP_TYPE_ID = "work"

        private fun getNotificationChannelGroupId(context: Context, typeId: String): String =
            "%s_%s_notification_group".format(context.packageName, typeId)


        private const val WORK_NOTIFY_CHANNEL_TYPE_ID = "work_%d"

        private fun getNotificationChannelId(context: Context, notifyDiv: LocalNotifyDiv): String =
            "%s_%s_notification_channel".format(
                context.packageName,
                WORK_NOTIFY_CHANNEL_TYPE_ID.format(notifyDiv.value)
            )
    }

    override fun initializeNotificationChannelsForAllLocalNotifyDiv() {
        //
        // タスク
        //

        // 通知用のチャンネルグループ
        val workNotificationChannelGroupId =
            getNotificationChannelGroupId(context, WORK_CHANNEL_GROUP_TYPE_ID)
        val workNotificationChannelGroupName =
            context.getString(R.string.work_notification_channel_group_name)
        manager.createNotificationChannelGroup(
            NotificationChannelGroup(
                workNotificationChannelGroupId,
                workNotificationChannelGroupName
            )
        )

        // 通知用のチャンネル
        // 当日のタスク
        val todayWorkNotificationChannelId =
            getNotificationChannelId(context, LocalNotifyDiv.TODAY_EVERY_DAY)
        val todayWorkNotificationChannelName =
            context.getString(R.string.today_work_notification_channel_name)
        val todayWorkNotificationChannelDescription =
            context.getString(R.string.today_work_notification_channel_description)
        val todayWorkNotificationChannel = NotificationChannel(
            todayWorkNotificationChannelId,
            todayWorkNotificationChannelName,
            // 優先度は音が鳴ればいい程度
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = todayWorkNotificationChannelDescription
            group = workNotificationChannelGroupId
        }
        // 翌日のタスク
        val tomorrowWorkNotificationChannelId =
            getNotificationChannelId(context, LocalNotifyDiv.TOMORROW_EVERY_DAY)
        val tomorrowWorkNotificationChannelName =
            context.getString(R.string.tomorrow_work_notification_channel_name)
        val tomorrowWorkNotificationChannelDescription =
            context.getString(R.string.tomorrow_work_notification_channel_description)
        val tomorrowWorkNotificationChannel = NotificationChannel(
            tomorrowWorkNotificationChannelId,
            tomorrowWorkNotificationChannelName,
            // 優先度は音が鳴ればいい程度
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = tomorrowWorkNotificationChannelDescription
            group = workNotificationChannelGroupId
        }

        // 通知チャネルを一括で作成
        manager.createNotificationChannels(
            listOf(
                todayWorkNotificationChannel,
                tomorrowWorkNotificationChannel
            )
        )
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun checkHasNotificationPermission(notifyDiv: LocalNotifyDiv): Boolean {
        // ユーザーがアプリの通知を有効にしていなければ通知の権限を確認する前もなく権限が足りていない状態
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        // OREO以降なら個別にチェックできるのでチェックする
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                manager.getNotificationChannel(getNotificationChannelId(context, notifyDiv))
            return channel?.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            // OREOより前なら通知は全体で一つなので、アプリに関して許可を出している現時点では許可されているものとみなす
            true
        }
    }

    override fun requestNotificationPermission(notifyDiv: LocalNotifyDiv) {
        // Android 12L(SDK 32)以前ではチャネル作成時に自動的にリクエストされるが、Android 13(SDK 33)以降では自動的にリクエストされないので注意が必要
        val notificationChannel =
            manager.getNotificationChannel(getNotificationChannelId(context, notifyDiv))
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannel.id)
        }
        context.startActivity(intent)
    }

    /**
     * 指定された[LocalNotifyDiv]の通知を行う。[expandedBody]が指定されている場合、通知の展開と縮小を切り替えることができるように通知される。
     */
    override fun notifyForLocalNotifyDiv(
        notifyDiv: LocalNotifyDiv,
        title: String,
        shrankBody: String,
        expandedBody: String?,
    ) {
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            // 開こうとしているActivityと同じActivityのインスタンスが存在する場合、そのActivityとその上のActivityをクリアして新しいActivityで起動する
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            when (notifyDiv) {
                LocalNotifyDiv.TODAY_EVERY_DAY -> {
                    putExtra(
                        WorkIndexRouting.SEARCH_DATE_INTENT_EXTRA_KEY,
                        // 今日の日にちを指定
                        DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now())
                    )
                }

                LocalNotifyDiv.TOMORROW_EVERY_DAY -> {
                    putExtra(
                        WorkIndexRouting.SEARCH_DATE_INTENT_EXTRA_KEY,
                        // 明日の日にちを指定
                        DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now().plusDays(1))
                    )
                }
            }
        }
        val activityActionPendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            WInPRequestCodeCategory.toNotifyRequestCode(notifyDiv),
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification =
            NotificationCompat.Builder(context, getNotificationChannelId(context, notifyDiv))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(shrankBody)
                // タスクなどはプライベートな内容なのでプライベート通知とする
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                // 優先度は音が鳴ればいい程度
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .apply {
                    if (expandedBody != null) {
                        setStyle(NotificationCompat.BigTextStyle().bigText(expandedBody))
                    }
                }
                // 通知をタップしたら通知を閉じる
                .setAutoCancel(true)
                // 通知をタップしたら指定したPendingIntentにしたがって開く
                .setContentIntent(activityActionPendingIntent)
                .build()

        if (checkHasNotificationPermission(notifyDiv)) {
            val notificationId = WInPRequestCodeCategory.toNotifyNotificationId(notifyDiv)
            manager.notify(notificationId, notification)
        }
    }
}