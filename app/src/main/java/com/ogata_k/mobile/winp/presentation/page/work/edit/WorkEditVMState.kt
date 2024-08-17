package com.ogata_k.mobile.winp.presentation.page.work.edit

import com.ogata_k.mobile.winp.presentation.enumerate.UiFormState
import com.ogata_k.mobile.winp.presentation.enumerate.UiInitializeState
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.ToUiState

data class WorkEditVMState(
    val initializeState: UiInitializeState,
    val formState: UiFormState,
    val workId: Int,
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
            initializeState = initializeState,
            formState = formState,
            isInCreating = workId == WorkEditRouting.CREATE_WORK_ID,
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
