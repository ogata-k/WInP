package com.ogata_k.mobile.winp.domain.use_case.local_notification

import com.ogata_k.mobile.winp.domain.use_case.AsyncUseCase

typealias RescheduleAllScheduledNotificationInput = Unit

typealias RescheduleAllScheduledNotificationOutput = Result<Unit>

/**
 * すでにスケジュールされている通知設定すべてを再度スケジュールするユースケース
 */
interface RescheduleAllScheduledNotificationAsyncUseCase :
    AsyncUseCase<RescheduleAllScheduledNotificationInput, RescheduleAllScheduledNotificationOutput>