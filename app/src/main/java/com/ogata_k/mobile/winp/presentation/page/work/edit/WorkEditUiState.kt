package com.ogata_k.mobile.winp.presentation.page.work.edit

import android.content.Context
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormData

data class WorkEditUiState(
    val initialized: Boolean,
    val isInCreating: Boolean,
    val formData: WorkFormData,
    val isInShowBeganDatePicker: Boolean,
    val isInShowBeganTimePicker: Boolean,
    val isInShowEndedDatePicker: Boolean,
    val isInShowEndedTimePicker: Boolean,
) {
    /**
     * フォーム画面のタイトルを取得
     */
    fun getFormTitle(context: Context): String? {
        if (!initialized) {
            // 初期化がまだなのでセットができない
            return null
        }

        return if (isInCreating) {
            context.getString(R.string.title_create_task)
        } else {
            context.getString(R.string.title_edit_task)
        }
    }
}
