package com.ogata_k.mobile.winp.presentation.page.work.edit

import com.ogata_k.mobile.winp.presentation.model.common.UiLoadingState
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.ToUiState

data class WorkEditVMState(
    val uiLoadingState: UiLoadingState,
    val workId: Int,
    val isInCreating: Boolean,
    val formData: WorkFormData,
    val validateExceptions: WorkFormValidateExceptions,
    val isInShowEditingTodoForm: Boolean,
    val isInShowBeganDatePicker: Boolean,
    val isInShowBeganTimePicker: Boolean,
    val isInShowEndedDatePicker: Boolean,
    val isInShowEndedTimePicker: Boolean,
) : ToUiState<WorkEditUiState> {
    override fun toUiState(): WorkEditUiState {
        return WorkEditUiState(
            uiLoadingState = uiLoadingState,
            isInCreating = isInCreating,
            formData = formData,
            validateExceptions = validateExceptions,
            isInShowEditingTodoForm = isInShowEditingTodoForm,
            isInShowBeganDatePicker = isInShowBeganDatePicker,
            isInShowBeganTimePicker = isInShowBeganTimePicker,
            isInShowEndedDatePicker = isInShowEndedDatePicker,
            isInShowEndedTimePicker = isInShowEndedTimePicker,
        )
    }
}
