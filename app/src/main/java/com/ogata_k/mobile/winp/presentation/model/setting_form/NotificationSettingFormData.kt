package com.ogata_k.mobile.winp.presentation.model.setting_form

import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import java.time.LocalTime

data class NotificationSettingFormData(
    val todayTime: LocalTime?,
    val tomorrowTime: LocalTime?,
)

data class NotificationSettingFormValidateExceptions(
    val todayTime: ValidationException,
    val tomorrowTime: ValidationException,
) {
    companion object {
        fun empty(): NotificationSettingFormValidateExceptions {
            return NotificationSettingFormValidateExceptions(
                todayTime = ValidationException.empty(),
                tomorrowTime = ValidationException.empty(),
            )
        }
    }
}