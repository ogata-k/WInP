package com.ogata_k.mobile.winp.presentation.constant

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv

/**
 * PendingIntentなどで固有の値が必要な時に使う値一覧
 */
class WInPRequestCodeCategory {
    companion object {
        /**
         * LocalNotifyDivをアラームに登録するときに利用する値
         */
        private const val ALARM_LOCAL_NOTIFY: Int = 100

        /**
         * アラームに登録するときの値に変換
         */
        fun toAlarmLocalNotificationRequestCode(notifyDiv: LocalNotifyDiv): Int =
            ALARM_LOCAL_NOTIFY + notifyDiv.value
    }
}