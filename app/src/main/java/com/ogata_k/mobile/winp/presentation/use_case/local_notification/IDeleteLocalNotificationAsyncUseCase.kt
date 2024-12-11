package com.ogata_k.mobile.winp.presentation.use_case.local_notification

import com.ogata_k.mobile.winp.domain.component.AlarmScheduler
import com.ogata_k.mobile.winp.domain.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.domain.use_case.local_notification.DeleteLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.DeleteLocalNotificationInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.DeleteLocalNotificationOutput
import kotlin.coroutines.cancellation.CancellationException

class IDeleteLocalNotificationAsyncUseCase(
    private val alarmScheduler: AlarmScheduler,
    private val dao: LocalNotificationDao
) :
    DeleteLocalNotificationAsyncUseCase {
    override suspend fun call(input: DeleteLocalNotificationInput): DeleteLocalNotificationOutput {
        try {
            val notifyDiv = input.notifyDiv

            dao.deleteLocalNotification(notifyDiv)

            // アラームはキャンセル
            alarmScheduler.cancel(notifyDiv)
            return DeleteLocalNotificationOutput.success(Unit)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return DeleteLocalNotificationOutput.failure(e)
        }
    }
}