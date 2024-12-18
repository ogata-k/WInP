package com.ogata_k.mobile.winp.presentation.use_case.work

import android.content.Context
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.formatter.buildFullDatePatternFormatter
import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.domain.component.LocalNotificationScheduler
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.use_case.work.GetSummaryAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetSummaryInput
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkOutput
import com.ogata_k.mobile.winp.presentation.model.work.WorkSummary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class INotifyForWorkAsyncUseCase(
    private val context: Context,
    private val localNotificationScheduler: LocalNotificationScheduler,
    private val getSummaryUseCase: GetSummaryAsyncUseCase,
) : NotifyForWorkAsyncUseCase {
    override suspend fun call(input: NotifyForWorkInput): NotifyForWorkOutput {
        val notifyDiv: LocalNotifyDiv = input.notifyDiv
        val targetRange: Pair<LocalDateTime, LocalDateTime> = when (notifyDiv) {
            LocalNotifyDiv.TODAY_EVERY_DAY -> {
                val today = LocalDate.now()
                Pair(today.atTime(LocalTime.MIN), today.atTime(LocalTime.MAX))
            }

            LocalNotifyDiv.TOMORROW_EVERY_DAY -> {
                val tomorrow = LocalDate.now().plusDays(1)
                Pair(tomorrow.atTime(LocalTime.MIN), tomorrow.atTime(LocalTime.MAX))
            }
        }
        val summary = getSummaryUseCase.call(
            GetSummaryInput(
                from = LocalDateTimeConverter.toOffsetDateTime(targetRange.first),
                to = LocalDateTimeConverter.toOffsetDateTime(targetRange.second),
            )
        ).getOrNull()?.let { WorkSummary.fromDomainModel(it) }

        localNotificationScheduler.notifyForLocalNotifyDiv(
            notifyDiv = notifyDiv,
            title = getNotifyTitle(context, notifyDiv),
            shrankBody = getNotifyShrankBody(context, notifyDiv, summary),
            expandedBody = getNotifyExpandedBody(context, notifyDiv, summary),
        )
        return Result.success(Unit)
    }

    /**
     * 通知タイトル
     */
    private fun getNotifyTitle(
        context: Context,
        notifyDiv: LocalNotifyDiv,
    ): String {
        return when (notifyDiv) {
            LocalNotifyDiv.TODAY_EVERY_DAY -> context.getString(R.string.today_work_notification_title)
            LocalNotifyDiv.TOMORROW_EVERY_DAY -> context.getString(R.string.tomorrow_work_notification_title)
        }
    }

    /**
     * 通知本文（展開していないときや展開するほどの長文ではないときに表示される）
     */
    private fun getNotifyShrankBody(
        context: Context,
        notifyDiv: LocalNotifyDiv,
        summary: WorkSummary?,
    ): String {
        if (summary == null) {
            return context.getString(R.string.notification_failed_fetch_summary)
        }

        val countUncompleted = summary.countUncompletedWork()
        return if (countUncompleted == 0) context.getString(R.string.notification_not_exist_uncompleted_task) else {
            context.getString(R.string.notification_exist_uncompleted_task).format(countUncompleted)
        }
    }

    /**
     * 通知本文（展開後の表示で使う）
     */
    private fun getNotifyExpandedBody(
        context: Context,
        notifyDiv: LocalNotifyDiv,
        summary: WorkSummary?,
    ): String? {
        if (summary == null) {
            return null
        }

        val countUncompleted = summary.countUncompletedWork()
        val countExpiredUncompleted = summary.countExpiredUncompletedWork()
        val countCompleted = summary.countCompletedWork()
        val countExpiredCompleted = summary.countExpiredCompletedWork()

        return arrayOf(
            context.getString(R.string.notification_fetched_date)
                .format(buildFullDatePatternFormatter().format(LocalDate.now())),

            context.getString(R.string.notification_count_uncompleted).format(countUncompleted),
            context.getString(R.string.notification_count_expired_uncompleted)
                .format(countExpiredUncompleted),
            context.getString(R.string.notification_count_completed).format(countCompleted),
            context.getString(R.string.notification_count_expired_completed)
                .format(countExpiredCompleted),
        ).joinToString(separator = "\n")
    }
}