package com.ogata_k.mobile.winp.presentation.use_case.work

import com.ogata_k.mobile.winp.domain.component.LocalNotificationScheduler
import com.ogata_k.mobile.winp.domain.use_case.work.GetSummaryAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkOutput
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class INotifyForWorkAsyncUseCase(
    private val localNotificationScheduler: LocalNotificationScheduler,
    private val getSummaryUseCase: GetSummaryAsyncUseCase,
) : NotifyForWorkAsyncUseCase {
    override suspend fun call(input: NotifyForWorkInput): NotifyForWorkOutput {

        localNotificationScheduler.notifyForLocalNotifyDiv(
            notifyDiv = input.notifyDiv,
            title = input.notifyDiv.toString(),
            shrankBody = "これはテスト通知です。",
            expandedBody = "This is a long long text at %s.\nLong Text Long Text.\nやっぱ長いね。".format(
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now())
            ),
        )
        return Result.success(Unit)
    }
}