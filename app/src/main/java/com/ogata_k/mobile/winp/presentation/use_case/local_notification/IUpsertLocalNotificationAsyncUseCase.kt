package com.ogata_k.mobile.winp.presentation.use_case.local_notification

import com.ogata_k.mobile.winp.domain.component.AlarmScheduler
import com.ogata_k.mobile.winp.domain.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.domain.use_case.local_notification.UpsertLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.UpsertLocalNotificationInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.UpsertLocalNotificationOutput
import com.ogata_k.mobile.winp.presentation.extention.scheduleReminder
import kotlin.coroutines.cancellation.CancellationException

class IUpsertLocalNotificationAsyncUseCase(
    private val alarmScheduler: AlarmScheduler,
    private val dao: LocalNotificationDao
) :
    UpsertLocalNotificationAsyncUseCase {
    override suspend fun call(input: UpsertLocalNotificationInput): UpsertLocalNotificationOutput {
        try {
            val notifyDiv = input.notifyDiv
            val notifyTime = input.notifyTime

            dao.upsertLocalNotification(notifyDiv, notifyTime)

            // アラームの設定
            notifyDiv.scheduleReminder(
                alarmScheduler = alarmScheduler,
                notifyTime = notifyTime.toLocalTime(),
                canSkipPastNotifyTime = true,
            )
            return UpsertLocalNotificationOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return UpsertLocalNotificationOutput.failure(e)
        }
    }
}
