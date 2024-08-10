package com.ogata_k.mobile.winp.presentation.page.work.edit

import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormData
import com.ogata_k.mobile.winp.presentation.page.ToUiState

data class WorkEditVMState(
    val initialized: Boolean,
    val workId: Int,
    val formData: WorkFormData,
    val isInShowBeganDatePicker: Boolean,
    val isInShowBeganTimePicker: Boolean,
    val isInShowEndedDatePicker: Boolean,
    val isInShowEndedTimePicker: Boolean,
) : ToUiState<WorkEditUiState> {
    override fun toUiState(): WorkEditUiState {
        return WorkEditUiState(
            initialized = initialized,
            isInCreating = workId == WorkEditRouting.CREATE_WORK_ID,
            formData = formData,
            isInShowBeganDatePicker = isInShowBeganDatePicker,
            isInShowBeganTimePicker = isInShowBeganTimePicker,
            isInShowEndedDatePicker = isInShowEndedDatePicker,
            isInShowEndedTimePicker = isInShowEndedTimePicker,
        )
    }
}
