package com.ogata_k.mobile.winp.domain.component

import android.app.PendingIntent
import android.content.Context
import java.time.LocalTime

interface AlarmScheduler {
    /**
     * 繰り返し実行できるようにスケジュールする。
     * Dozeモード中は実行されず、後から実行されることの注意。
     *
     * [canSkipPastNotifyTime]にtrueを指定すると、時間設定時にその日の時間が過ぎていたらスキップする
     */
    fun scheduleInexactRepeating(
        requestCode: Int,
        alarmType: Int,
        notifyTime: LocalTime,
        canSkipPastNotifyTime: Boolean,
        intervalMills: Long,
        intentBuilder: (context: Context, requestCode: Int) -> PendingIntent
    )

    /**
     * スケージュールした設定をキャンセルする
     */
    fun cancel(
        requestCode: Int,
        intentBuilder: (context: Context, requestCode: Int) -> PendingIntent
    )
}