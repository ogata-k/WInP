package com.ogata_k.mobile.winp.domain.component

import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import java.time.LocalTime

interface AlarmScheduler {
    /**
     * 繰り返し実行できるようにスケジュールする。
     * Dozeモード中は実行されず、後から実行されることの注意。
     *
     * [canSkipPastNotifyTime]にtrueを指定すると、時間設定時にその日の時間が過ぎていたらスキップする
     */
    fun scheduleLocalNotifyInexactRepeating(
        notifyDiv: LocalNotifyDiv,
        notifyTime: LocalTime,
    )

    /**
     * スケージュールした設定をキャンセルする
     */
    fun cancel(notifyDiv: LocalNotifyDiv)
}