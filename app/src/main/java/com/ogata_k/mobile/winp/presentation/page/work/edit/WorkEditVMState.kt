package com.ogata_k.mobile.winp.presentation.page.work.edit

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.IVMState

data class WorkEditVMState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val workId: Long,
    val copyFromWorkId: Long?,
    val isInCreating: Boolean,
    val formData: WorkFormData,
    val validateExceptions: WorkFormValidateExceptions,
    val isInShowEditingTodoForm: Boolean,
    val isInShowBeganDatePicker: Boolean,
    val isInShowBeganTimePicker: Boolean,
    val isInShowEndedDatePicker: Boolean,
    val isInShowEndedTimePicker: Boolean,
) : IVMState<ScreenLoadingState, ScreenLoadingState, WorkEditUiState> {
    override fun toUiState(): WorkEditUiState {
        return WorkEditUiState(
            loadingState = loadingState,
            basicState = basicState,
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
