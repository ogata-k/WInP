package com.ogata_k.mobile.winp.presentation.use_case.local_notification

import com.ogata_k.mobile.winp.domain.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.domain.use_case.local_notification.GetLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.GetLocalNotificationInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.GetLocalNotificationOutput
import kotlin.coroutines.cancellation.CancellationException

class IGetLocalNotificationAsyncUseCase(private val dao: LocalNotificationDao) :
    GetLocalNotificationAsyncUseCase {
    override suspend fun call(input: GetLocalNotificationInput): GetLocalNotificationOutput {
        try {
            val localNotification = dao.findLocalNotification(input.notifyDiv)
            return GetLocalNotificationOutput.success(localNotification)
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return GetLocalNotificationOutput.failure(e)
        }
    }
}