package com.ogata_k.mobile.winp.presentation.page.setting.notification

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.page.IUiState
import java.time.LocalTime

data class NotificationSettingUiState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val todayNotifyTime: LocalTime?,
    val needRequestTodayNotifyPermission: Boolean,
    val isInShowTodayTimePicker: Boolean,
    val isInShowClearTodayConfirmDialog: Boolean,
    val tomorrowNotifyTime: LocalTime?,
    val needRequestTomorrowNotifyPermission: Boolean,
    val isInShowTomorrowTimePicker: Boolean,
    val isInShowClearTomorrowConfirmDialog: Boolean,
) : IUiState<ScreenLoadingState>
