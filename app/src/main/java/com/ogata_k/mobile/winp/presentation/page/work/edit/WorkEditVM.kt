package com.ogata_k.mobile.winp.presentation.page.work.edit

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.presentation.enumerate.UiFormState
import com.ogata_k.mobile.winp.presentation.enumerate.UiInitializeState
import com.ogata_k.mobile.winp.presentation.enumerate.UiNextScreenState
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationExceptionType
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkTodoFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkTodoFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class WorkEditVM @Inject constructor() : AbstractViewModel<WorkEditVMState, WorkEditUiState>() {
    companion object {
        const val TITLE_MAX_LENGTH = 30

        const val DESCRIPTION_MAX_LENGTH = 2000

        const val WORK_TODO_ITEM_DESCRIPTION_MAX_LENGTH = 50
    }

    override val viewModelStateFlow: MutableStateFlow<WorkEditVMState> =
        MutableStateFlow(
            WorkEditVMState(
                // 初期状態は未初期化状態とする
                initializeState = UiInitializeState.LOADING,
                screenState = UiNextScreenState.LOADING,
                formState = UiFormState.NOT_INITIALIZE,
                isInCreating = isInCreating(WorkEditRouting.CREATE_WORK_ID),
                workId = WorkEditRouting.CREATE_WORK_ID,
                formData = WorkFormData.empty(),
                validateExceptions = WorkFormValidateExceptions.empty(),
                isInShowEditingTodoForm = false,
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
        if (vmState.initializeState.isInitialized()) {
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
                editingTodoItem = WorkTodoFormData.empty(UUID.randomUUID()),
                todoItems = emptyList(),
            )
            val newVmState = vmState.copy(
                initializeState = UiInitializeState.INITIALIZED,
                screenState = UiNextScreenState.INITIALIZED,
                formState = UiFormState.FORM_EDITING,
                isInCreating = true,
                workId = workId,
                formData = formData,
                validateExceptions = validateFormData(formData, vmState.isInShowEditingTodoForm),
            )

            updateVMState(newVmState)
            // 作成中なのでDBなど見に行く必要はなし
            return
        }

        val newVmState = vmState.copy(isInCreating = false, workId = workId)
        updateVMState(newVmState)

        // DBデータでFormの初期化をしたときに初期化を完了とする
        // TODO 実際の実装にする
        viewModelScope.launch {
            delay(500)
            val todoItems: MutableList<WorkTodoFormData> = mutableListOf()
            (0..5).forEach { index ->
                todoItems.add(
                    WorkTodoFormData(
                        uuid = generateNotUsingWorkTodoUuid(),
                        id = index + 1,
                        description = "対応するTODO%d".format(index + 1),
                        isCompleted = index % 3 == 0,
                    )
                )
            }
            // 編集用のフォームデータ
            // TODO これらの値はDBなどから取ってきた値にする
            val formData = WorkFormData(
                title = "編集中",
                description = "これは編集中タスクの説明です。",
                beganDate = LocalDate.now(),
                beganTime = LocalTime.now(),
                endedDate = null,
                endedTime = null,
                editingTodoItem = WorkTodoFormData.empty(UUID.randomUUID()),
                todoItems = todoItems,
            )
            updateVMState(
                readVMState().copy(
                    initializeState = UiInitializeState.INITIALIZED,
                    screenState = UiNextScreenState.INITIALIZED,
                    formState = UiFormState.FORM_EDITING,
                    formData = formData,
                    validateExceptions = validateFormData(
                        formData,
                        readVMState().isInShowEditingTodoForm
                    ),
                )
            )
        }
    }

    /**
     * FormDataのタイトルを更新
     */
    fun updateFormTitle(value: String) {
        val oldVmState = readVMState()
        val newFormData = oldVmState.formData.copy(title = value)
        val newVmState = oldVmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, oldVmState.isInShowEditingTodoForm),
        )

        updateVMState(newVmState)
    }

    /**
     * FormDataの説明を更新
     */
    fun updateFormDescription(value: String) {
        val oldVmState = readVMState()
        val newFormData = oldVmState.formData.copy(description = value)
        val newVmState = oldVmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, oldVmState.isInShowEditingTodoForm),
        )

        updateVMState(newVmState)
    }

    /**
     * uuidを指定して保持している一覧データから検索し、あれば取得
     */
    private fun searchWorkTodoFormByUuid(uuid: UUID): WorkTodoFormData? {
        return readVMState().formData.todoItems.firstOrNull { it.uuid == uuid }
    }

    /**
     * 指定されたUUIDのタスクTODOが存在するならtrue
     */
    private fun existWorkTodoForm(uuid: UUID): Boolean {
        return searchWorkTodoFormByUuid(uuid) !== null
    }

    /**
     * 新規タスクTODO作成用にUUIDを生成
     */
    private fun generateNotUsingWorkTodoUuid(): UUID {
        while (true) {
            val uuid = UUID.randomUUID()
            // すでに存在するものは利用できないようにする
            if (existWorkTodoForm(uuid)) {
                continue
            }
            return uuid
        }
    }

    /**
     * 作成として、タスクTODOのフォームの表示させる。
     * UUIDを指定していないならフォームを非表示にする。
     */
    fun showWorkTodoCreateForm() {
        showWorkTodoForm(uuid = generateNotUsingWorkTodoUuid())
    }

    /**
     * 指定したUUIDのタスクがあればそれを編集なければ作成として、タスクTODOのフォームの表示させる。
     * UUIDを指定していないならフォームを非表示にする。
     */
    fun showWorkTodoForm(uuid: UUID?) {
        if (uuid == null) {
            val vmState = readVMState()
            val newVmState = vmState.copy(isInShowEditingTodoForm = false)
            updateVMState(newVmState)
            return
        }

        val vmState = readVMState()
        val newFormData = vmState.formData.copy(
            editingTodoItem = searchWorkTodoFormByUuid(uuid)
                ?: WorkTodoFormData.empty(uuid)
        )
        val newVmState = vmState.copy(
            isInShowEditingTodoForm = true,
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, vmState.isInShowEditingTodoForm),
        )
        updateVMState(newVmState)
    }

    /**
     * 作成/編集中の対象となっているタスクTODOの完了状態を更新する
     */
    fun updateWorkTodoFormCompleted(isCompleted: Boolean) {
        val vmState = readVMState()
        val newWorkTodoFormData = vmState.formData.editingTodoItem.copy(
            isCompleted = isCompleted,
        )
        val newFormData = vmState.formData.copy(
            editingTodoItem = newWorkTodoFormData,
        )
        val newVmState = vmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, vmState.isInShowEditingTodoForm),
        )
        updateVMState(newVmState)
    }

    /**
     * 作成/編集中の対象となっているタスクTODOの説明内容を更新する
     */
    fun updateWorkTodoFormDescription(value: String) {
        val vmState = readVMState()
        val newWorkTodoFormData = vmState.formData.editingTodoItem.copy(
            description = value,
        )
        val newFormData = vmState.formData.copy(
            editingTodoItem = newWorkTodoFormData,
        )
        val newVmState = vmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, vmState.isInShowEditingTodoForm),
        )
        updateVMState(newVmState)
    }

    /**
     * 作成/編集中の対象となっているタスクTODOをリストに適用する
     */
    fun applyEditingToWorkTodoItems() {
        val vmState = readVMState()
        if (!vmState.isInShowEditingTodoForm) {
            // フォームを利用している状態ではないので無視
            return
        }

        val editingWorkTodoFormData = vmState.formData.editingTodoItem
        if (vmState.validateExceptions.editingTodoItem.hasError()) {
            // エラーがある場合は実行しない
            return
        }

        val editingWorkTodoUuid = editingWorkTodoFormData.uuid
        val newTodoItems: MutableList<WorkTodoFormData> = mutableListOf()
        var contains = false
        vmState.formData.todoItems.forEach {
            if (it.uuid == editingWorkTodoUuid) {
                newTodoItems.add(editingWorkTodoFormData)
                contains = true
            } else {
                newTodoItems.add(it)
            }
        }

        if (!contains) {
            newTodoItems.add(editingWorkTodoFormData)
        }

        val newVmState = vmState.copy(
            // 問題なくリストを更新できたのでフォームを非表示にする
            isInShowEditingTodoForm = false,
            formData = vmState.formData.copy(
                todoItems = newTodoItems,
            )
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
        val newFormData = vmState.formData.copy(beganDate = date)
        val newVmState = vmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, vmState.isInShowEditingTodoForm),
        )
        updateVMState(newVmState)
    }

    /**
     * 開始日時の時間を更新
     */
    fun updateBeganTime(time: LocalTime?) {
        val vmState = readVMState()
        val newFormData = vmState.formData.copy(beganTime = time)
        val newVmState = vmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, vmState.isInShowEditingTodoForm),
        )
        updateVMState(newVmState)
    }

    /**
     * 終了日時の日にちを更新
     */
    fun updateEndedDate(date: LocalDate?) {
        val vmState = readVMState()
        val newFormData = vmState.formData.copy(endedDate = date)
        val newVmState = vmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, vmState.isInShowEditingTodoForm),
        )
        updateVMState(newVmState)
    }

    /**
     * 終了日時の時間を更新
     */
    fun updateEndedTime(time: LocalTime?) {
        val vmState = readVMState()
        val newFormData = vmState.formData.copy(endedTime = time)
        val newVmState = vmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, vmState.isInShowEditingTodoForm),
        )
        updateVMState(newVmState)
    }

    /**
     * 入力内容をバリデーションする
     */
    private fun validateFormData(
        formData: WorkFormData,
        isInShowEditingTodoForm: Boolean
    ): WorkFormValidateExceptions {
        val titleValidated = if (formData.title.isEmpty()) ValidationException.of(
            ValidationExceptionType.EmptyValue(false)
        )
        else if (formData.title.length > TITLE_MAX_LENGTH) ValidationException.of(
            ValidationExceptionType.OverflowValue(
                TITLE_MAX_LENGTH, false
            )
        )
        else ValidationException.empty()

        val descriptionValidated = if (formData.description.isEmpty()) ValidationException.of(
            ValidationExceptionType.EmptyValue(false)
        )
        else if (formData.description.length > DESCRIPTION_MAX_LENGTH) ValidationException.of(
            ValidationExceptionType.OverflowValue(
                DESCRIPTION_MAX_LENGTH, false
            )
        )
        else ValidationException.empty()

        val beganDateTimeValidated = if (formData.beganDate != null) {
            if (formData.endedDate != null && LocalDateTime.of(
                    formData.beganDate,
                    formData.beganTime ?: LocalTime.MIN
                ) >= LocalDateTime.of(formData.endedDate, formData.endedTime ?: LocalTime.MAX)
            ) {
                ValidationException.of(
                    ValidationExceptionType.NeedSmallerThanDatetime(
                        LocalDateTime.of(
                            formData.endedDate,
                            formData.endedTime ?: LocalTime.MAX
                        )
                    )
                )
            } else {
                ValidationException.empty()
            }
        } else if (formData.beganTime != null) {
            ValidationException.of(ValidationExceptionType.NeedDateInput)
        } else {
            ValidationException.empty()
        }

        val endedDateTimeValidated = if (formData.endedDate != null) {
            if (formData.beganDate != null && LocalDateTime.of(
                    formData.beganDate,
                    formData.beganTime ?: LocalTime.MIN
                ) >= LocalDateTime.of(formData.endedDate, formData.endedTime ?: LocalTime.MAX)
            ) {
                ValidationException.of(
                    ValidationExceptionType.NeedBiggerThanDatetime(
                        LocalDateTime.of(
                            formData.beganDate,
                            formData.beganTime ?: LocalTime.MIN
                        )
                    )
                )
            } else {
                ValidationException.empty()
            }
        } else if (formData.endedTime != null) {
            ValidationException.of(ValidationExceptionType.NeedDateInput)
        } else {
            ValidationException.empty()
        }

        val editingTodoItemValidated = if (isInShowEditingTodoForm) {
            val workTodoFormData = formData.editingTodoItem
            @Suppress("NAME_SHADOWING") val descriptionValidated =
                if (workTodoFormData.description.isEmpty()) ValidationException.of(
                    ValidationExceptionType.EmptyValue(false)
                )
                else if (workTodoFormData.description.length > WORK_TODO_ITEM_DESCRIPTION_MAX_LENGTH) ValidationException.of(
                    ValidationExceptionType.OverflowValue(
                        WORK_TODO_ITEM_DESCRIPTION_MAX_LENGTH, false
                    )
                )
                else ValidationException.empty()

            WorkTodoFormValidateExceptions(
                description = descriptionValidated,
                isCompleted = ValidationException.empty(),
            )
        } else {
            WorkTodoFormValidateExceptions.empty()
        }

        return WorkFormValidateExceptions(
            title = titleValidated,
            description = descriptionValidated,
            beganDateTime = beganDateTimeValidated,
            endedDateTime = endedDateTimeValidated,
            editingTodoItem = editingTodoItemValidated,
        )
    }

    /**
     * Workの作成もしくは更新を行う
     */
    fun createOrUpdateWorkItem() {
        val vmState = readVMState()
        if (vmState.validateExceptions.hasError()) {
            // エラーがある場合は作成や更新の処理を行わずに終了
            return
        }

        val newVmState = vmState.copy(formState = UiFormState.DOING_ACTION)
        updateVMState(newVmState)

        // TODO 実際の実装にする
        viewModelScope.launch {
            // TODO 作成や更新をする
            delay(3000)

            // TODO 作成や更新中に画面操作できないようにローディング表示させる

            // TODO 作成や更新の結果を通知する
            val result = Random.nextInt() % 3 != 0
            val oldVmState = readVMState()
            updateVMState(
                oldVmState.copy(
                    screenState = if (result) (if (oldVmState.isInCreating) UiNextScreenState.CREATED else UiNextScreenState.UPDATED) else UiNextScreenState.ERROR,
                    formState = if (result) UiFormState.SUCCESS_ACTION else UiFormState.FAIL_ACTION,
                )
            )
        }
    }

    /**
     * フォームを操作可能な状態にする
     */
    fun updateToEditingFormState() {
        val vmState = readVMState()
        val newVmState = vmState.copy(formState = UiFormState.FORM_EDITING)
        updateVMState(newVmState)
    }
}