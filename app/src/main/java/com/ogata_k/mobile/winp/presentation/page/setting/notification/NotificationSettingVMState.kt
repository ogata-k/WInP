package com.ogata_k.mobile.winp.presentation.page.setting.notification

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.page.IVMState
import java.time.LocalTime

data class NotificationSettingVMState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val todayNotifyTime: LocalTime?,
    val isInShowTodayTimePicker: Boolean,
    val isInShowClearTodayConfirmDialog: Boolean,
    val tomorrowNotifyTime: LocalTime?,
    val isInShowTomorrowTimePicker: Boolean,
    val isInShowClearTomorrowConfirmDialog: Boolean,
) : IVMState<ScreenLoadingState, ScreenLoadingState, NotificationSettingUiState> {
    override fun toUiState(): NotificationSettingUiState {
        return NotificationSettingUiState(
            loadingState = loadingState,
            basicState = basicState,
            todayNotifyTime = todayNotifyTime,
            isInShowTodayTimePicker = isInShowTodayTimePicker,
            isInShowClearTodayConfirmDialog = isInShowClearTodayConfirmDialog,
            tomorrowNotifyTime = tomorrowNotifyTime,
            isInShowTomorrowTimePicker = isInShowTomorrowTimePicker,
            isInShowClearTomorrowConfirmDialog = isInShowClearTomorrowConfirmDialog,
        )
    }
}
