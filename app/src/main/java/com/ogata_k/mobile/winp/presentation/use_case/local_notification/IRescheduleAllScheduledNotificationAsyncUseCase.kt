package com.ogata_k.mobile.winp.presentation.use_case.local_notification

import com.ogata_k.mobile.winp.domain.component.AlarmScheduler
import com.ogata_k.mobile.winp.domain.model.notification.LocalNotification
import com.ogata_k.mobile.winp.domain.use_case.local_notification.FetchAllLocalNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.FetchAllLocalNotificationInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RescheduleAllScheduledNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RescheduleAllScheduledNotificationInput
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RescheduleAllScheduledNotificationOutput
import com.ogata_k.mobile.winp.presentation.extention.scheduleReminder

class IRescheduleAllScheduledNotificationAsyncUseCase(
    private val alarmScheduler: AlarmScheduler,
    private val fetchAllLocalNotificationUseCase: FetchAllLocalNotificationAsyncUseCase,
) : RescheduleAllScheduledNotificationAsyncUseCase {
    override suspend fun call(input: RescheduleAllScheduledNotificationInput): RescheduleAllScheduledNotificationOutput {
        val allSettingsResult: Result<List<LocalNotification>> = fetchAllLocalNotificationUseCase
            .call(FetchAllLocalNotificationInput)
        if (allSettingsResult.isFailure) {
            return Result.failure(allSettingsResult.exceptionOrNull()!!)
        }
        allSettingsResult.getOrThrow().forEach { setting ->
            // アラームの設定
            setting.localNotifyDiv.scheduleReminder(
                alarmScheduler = alarmScheduler,
                notifyTime = setting.notifyTime.toLocalTime(),
            )
        }

        return Result.success(Unit)
    }
}