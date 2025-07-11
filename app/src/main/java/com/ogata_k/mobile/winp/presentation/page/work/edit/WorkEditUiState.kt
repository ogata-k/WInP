package com.ogata_k.mobile.winp.presentation.page.work.edit

import android.content.Context
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.IUiState
import java.util.UUID

data class WorkEditUiState(
    override val loadingState: ScreenLoadingState,
    override val basicState: BasicScreenState,
    val isInCreating: Boolean,
    val formData: WorkFormData,
    val validateExceptions: WorkFormValidateExceptions,
    val isInShowEditingTodoForm: Boolean,
    val isInShowBeganDatePicker: Boolean,
    val isInShowBeganTimePicker: Boolean,
    val isInShowEndedDatePicker: Boolean,
    val isInShowEndedTimePicker: Boolean,
) : IUiState<ScreenLoadingState> {
    val editingTodoItemUuid: UUID = formData.editingTodoItem.uuid

    /**
     * フォーム画面のタイトルを取得
     */
    fun getFormTitle(context: Context): String? {
        if (!loadingState.isInitialized()) {
            // 初期化がまだなのでセットができない
            return null
        }

        return if (isInCreating) {
            context.getString(R.string.title_create_work)
        } else {
            context.getString(R.string.title_edit_work)
        }
    }

    /**
     * trueなら作成中。falseなら編集中
     */
    fun isInCreateWorkTodoForm(uuid: UUID): Boolean {
        return formData.todoItems.firstOrNull { it.uuid == uuid } == null
    }
}
