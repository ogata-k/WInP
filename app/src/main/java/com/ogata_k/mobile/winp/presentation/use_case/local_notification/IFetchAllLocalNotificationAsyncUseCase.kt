package com.ogata_k.mobile.winp.presentation.use_case.local_notification

import com.ogata_k.mobile.winp.domain.infra.database.dao.LocalNotificationDao
import com.ogata_k.mobile.winp.domain.use_case.local_notification.FetchAllLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.FetchAllLocalNotificationInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.FetchAllLocalNotificationOutput
import kotlin.coroutines.cancellation.CancellationException

class IFetchAllLocalNotificationAsyncUseCase(private val dao: LocalNotificationDao) :
    FetchAllLocalNotificationAsyncUseCase {
    override suspend fun call(input: FetchAllLocalNotificationInput): FetchAllLocalNotificationOutput {
        try {
            return FetchAllLocalNotificationOutput.success(dao.fetchAll())
        } catch (e: CancellationException) {
            // suspendなので中断される可能性を考慮。中断時はキャンセル用のエラーが渡ってくるのでそのまま投げる
            throw e
        } catch (e: Throwable) {
            return FetchAllLocalNotificationOutput.failure(e)
        }
    }
}