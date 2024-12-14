package com.ogata_k.mobile.winp.presentation.constant

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv

/**
 * PendingIntentなどで固有の値が必要な時に使う値一覧
 */
class WInPRequestCodeCategory {
    companion object {
        /**
         * [LocalNotifyDiv]をもとに通知をたたくためのアラームを登録するときに利用する値
         */
        private const val ALARM_LOCAL_NOTIFY: Int = 100

        /**
         * アラームに登録するときの値に変換
         */
        fun toAlarmLocalNotificationRequestCode(notifyDiv: LocalNotifyDiv): Int =
            ALARM_LOCAL_NOTIFY + notifyDiv.value

        /**
         * [LocalNotifyDiv]の通知処理を行う時のPendingIntent用のリクエストコード
         */
        private const val LOCAL_NOTIFY_REQUEST_CODE: Int = 200

        /**
         * 通知するときのPendingIntent用のリクエストコードに変換
         */
        fun toNotifyRequestCode(notifyDiv: LocalNotifyDiv): Int =
            LOCAL_NOTIFY_REQUEST_CODE + notifyDiv.value

        /**
         * [LocalNotifyDiv]の通知処理を行う時の通知ID
         */
        private const val LOCAL_NOTIFY_NOTIFICATION_ID: Int = 300

        /**
         * 通知するときの値に変換
         */
        fun toNotifyNotificationId(notifyDiv: LocalNotifyDiv): Int =
            LOCAL_NOTIFY_NOTIFICATION_ID + notifyDiv.value
    }
}