package com.ogata_k.mobile.winp.presentation.page.work.edit

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkTodoFormData
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class WorkEditVM @Inject constructor() : AbstractViewModel<WorkEditVMState, WorkEditUiState>() {
    override val viewModelStateFlow: MutableStateFlow<WorkEditVMState> =
        MutableStateFlow(
            WorkEditVMState(
                // 初期状態は未初期化状態とする
                initialized = false,
                workId = WorkEditRouting.CREATE_WORK_ID,
                formData = WorkFormData.empty(),
                isInShowBeganDatePicker = false,
                isInShowBeganTimePicker = false,
                isInShowEndedDatePicker = false,
                isInShowEndedTimePicker = false,
            )
        )

    override val uiStateFlow: StateFlow<WorkEditUiState> =
        asUIStateFlow(viewModelScope, viewModelStateFlow)

    /**
     * タスク作成中かどうかを判定
     */
    private fun isInCreating(workId: Int): Boolean = workId == WorkEditRouting.CREATE_WORK_ID

    /**
     * FormDataを初期化
     */
    fun initializeForm(workId: Int) {
        val vmState = readVMState()
        if (vmState.initialized) {
            // 初期化済みなので追加対応の必要なし
            return
        }

        if (isInCreating(workId)) {
            // 作成用のフォームデータ
            val formData = WorkFormData(
                title = "",
                description = "",
                beganDate = null,
                beganTime = null,
                endedDate = null,
                endedTime = null,
                todoItems = emptyList(),
            )
            val newVmState = vmState.copy(
                workId = workId,
                initialized = true,
                formData = formData,
            )
            updateVMState(newVmState)
            // 作成中なのでDBなど見に行く必要はなし
            return
        }

        val newVmState = vmState.copy(workId = workId)
        updateVMState(newVmState)

        // DBデータでFormの初期化をしたときに初期化を完了とする
        // TODO 実際の実装にする
        viewModelScope.launch {
            delay(500)
            // 編集用のフォームデータ
            // TODO これらの値はDBなどから取ってきた値にする
            val formData = WorkFormData(
                title = "編集中",
                description = "これは編集中タスクの説明です。",
                beganDate = LocalDate.now(),
                beganTime = LocalTime.now(),
                endedDate = null,
                endedTime = null,
                todoItems = listOf(
                    WorkTodoFormData(
                        id = 1,
                        todoTask = "対応するTODO１",
                        isCompleted = false,
                    ),
                    WorkTodoFormData(
                        id = 2,
                        todoTask = "対応するTODO２",
                        isCompleted = true,
                    ),
                ),
            )
            updateVMState(
                readVMState().copy(
                    initialized = true,
                    formData = formData,
                )
            )
        }
    }

    /**
     * FormDataのタイトルを更新
     */
    fun updateFormTitle(value: String) {
        val oldVmState = readVMState()
        val newVmState = oldVmState.copy(
            formData = oldVmState.formData.copy(title = value),
        )

        updateVMState(newVmState)
    }

    /**
     * FormDataの説明を更新
     */
    fun updateFormDescription(value: String) {
        val oldVmState = readVMState()
        val newVmState = oldVmState.copy(
            formData = oldVmState.formData.copy(description = value),
        )

        updateVMState(newVmState)
    }

    /**
     * 開始日時の日にち選択ダイアログの表示切替（falseなら非表示化）
     */
    fun showBeganDatePicker(show: Boolean = true) {
        val vmState = readVMState()
        val newVmState = vmState.copy(isInShowBeganDatePicker = show)
        updateVMState(newVmState)
    }

    /**
     * 開始日時の時間選択ダイアログの表示切替（falseなら非表示化）
     */
    fun showBeganTimePicker(show: Boolean = true) {
        val vmState = readVMState()
        val newVmState = vmState.copy(isInShowBeganTimePicker = show)
        updateVMState(newVmState)
    }

    /**
     * 終了日時の日にち選択ダイアログの表示切替（falseなら非表示化）
     */
    fun showEndedDatePicker(show: Boolean = true) {
        val vmState = readVMState()
        val newVmState = vmState.copy(isInShowEndedDatePicker = show)
        updateVMState(newVmState)
    }

    /**
     * 終了日時の時間選択ダイアログの表示切替（falseなら非表示化）
     */
    fun showEndedTimePicker(show: Boolean = true) {
        val vmState = readVMState()
        val newVmState = vmState.copy(isInShowEndedTimePicker = show)
        updateVMState(newVmState)
    }

    /**
     * 開始日時の日にちを更新
     */
    fun updateBeganDate(date: LocalDate?) {
        val vmState = readVMState()
        val newVmState = vmState.copy(
            formData = vmState.formData.copy(beganDate = date)
        )
        updateVMState(newVmState)
    }

    /**
     * 開始日時の時間を更新
     */
    fun updateBeganTime(time: LocalTime?) {
        val vmState = readVMState()
        val newVmState = vmState.copy(
            formData = vmState.formData.copy(beganTime = time)
        )
        updateVMState(newVmState)
    }

    /**
     * 終了日時の日にちを更新
     */
    fun updateEndedDate(date: LocalDate?) {
        val vmState = readVMState()
        val newVmState = vmState.copy(
            formData = vmState.formData.copy(endedDate = date)
        )
        updateVMState(newVmState)
    }

    /**
     * 終了日時の時間を更新
     */
    fun updateEndedTime(time: LocalTime?) {
        val vmState = readVMState()
        val newVmState = vmState.copy(
            formData = vmState.formData.copy(endedTime = time)
        )
        updateVMState(newVmState)
    }

}