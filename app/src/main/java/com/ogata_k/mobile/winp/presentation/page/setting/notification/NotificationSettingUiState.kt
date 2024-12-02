package com.ogata_k.mobile.winp.presentation.page.setting.notification

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.setting_form.NotificationSettingFormData
import com.ogata_k.mobile.winp.presentation.model.setting_form.NotificationSettingFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.IUiState

data class NotificationSettingUiState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val validateExceptions: NotificationSettingFormValidateExceptions,
    val formData: NotificationSettingFormData,
) : IUiState<ScreenLoadingState> {
    // @todo
}
