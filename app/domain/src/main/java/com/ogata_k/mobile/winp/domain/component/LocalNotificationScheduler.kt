package com.ogata_k.mobile.winp.domain.component

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv

interface LocalNotificationScheduler {
    /**
     * すべての[LocalNotifyDiv]の値において通知チャネルを初期化する（SDK 26~）
     * channelBuilderがnot nullを返す時だけ作成する。
     */
    fun initializeNotificationChannelsForAllLocalNotifyDiv()

    /**
     * 通知権限があるかどうかを判定
     */
    fun checkHasNotificationPermission(notifyDiv: LocalNotifyDiv): Boolean

    /**
     * 通知権限を要求
     */
    fun requestNotificationPermission(notifyDiv: LocalNotifyDiv)

    /**
     * 指定された[LocalNotifyDiv]の通知を行う。[expandedBody]が指定されている場合、通知の展開と縮小を切り替えることができるように通知される。
     */
    fun notifyForLocalNotifyDiv(
        notifyDiv: LocalNotifyDiv,
        title: String,
        shrankBody: String,
        expandedBody: String? = null,
    )
}