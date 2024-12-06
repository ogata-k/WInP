package com.ogata_k.mobile.winp.presentation.page.setting.notification

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.page.IVMState
import java.time.LocalTime

data class NotificationSettingVMState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val todayNotifyTime: LocalTime?,
    val tomorrowNotifyTime: LocalTime?,
) : IVMState<ScreenLoadingState, ScreenLoadingState, NotificationSettingUiState> {
    override fun toUiState(): NotificationSettingUiState {
        return NotificationSettingUiState(
            loadingState = loadingState,
            basicState = basicState,
            todayNotifyTime = todayNotifyTime,
            tomorrowNotifyTime = tomorrowNotifyTime,
        )
    }
}
