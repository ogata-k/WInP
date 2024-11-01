package com.ogata_k.mobile.winp.presentation.page.work.edit

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkInput
import com.ogata_k.mobile.winp.presentation.enumerate.ActionDoneResult
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationExceptionType
import com.ogata_k.mobile.winp.presentation.event.EventBus
import com.ogata_k.mobile.winp.presentation.event.work.FailedCreateWork
import com.ogata_k.mobile.winp.presentation.event.work.FailedUpdateWork
import com.ogata_k.mobile.winp.presentation.event.work.SucceededCreateWork
import com.ogata_k.mobile.winp.presentation.event.work.SucceededUpdateWork
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkTodoFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkTodoFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkEditVM @Inject constructor(
    private val getWorkUseCase: GetWorkAsyncUseCase,
    private val createWorkUseCase: CreateWorkAsyncUseCase,
    private val updateWorkUseCase: UpdateWorkAsyncUseCase,
) : AbstractViewModel<ScreenLoadingState, WorkEditVMState, ScreenLoadingState, WorkEditUiState>() {
    companion object {
        const val TITLE_MAX_LENGTH = 30

        const val DESCRIPTION_MAX_LENGTH = 2000

        const val WORK_TODO_ITEM_DESCRIPTION_MAX_LENGTH = 50
    }

    override val viewModelStateFlow: MutableStateFlow<WorkEditVMState> = MutableStateFlow(
        WorkEditVMState(
            // 初期状態は未初期化状態とする
            loadingState = ScreenLoadingState.READY,
            basicState = BasicScreenState.initialState(),
            isInCreating = true,
            workId = AsCreate.CREATING_ID,
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
     * workIdを初期化
     */
    fun setWorkId(workId: Long) {
        val vmState = readVMState()
        updateVMState(vmState.copy(workId = workId))
    }

    override fun initializeVM() {
        var vmState = readVMState()
        if (vmState.loadingState.isInitialized()) {
            // 初期化済みなので追加対応の必要なし
            return
        }
        // 事前にsetWorkId()で設定しておく必要がある
        val workId = vmState.workId

        if (isInCreating(workId)) {
            // 作成中
            vmState = vmState.copy(basicState = vmState.basicState.toDoingAction())
            updateVMState(vmState)

            // 作成用のフォームデータ
            val formData = WorkFormData(
                id = workId,
                title = "",
                description = "",
                beganDate = null,
                beganTime = null,
                endedDate = null,
                endedTime = null,
                completedAt = null,
                editingTodoItem = WorkTodoFormData.empty(UUID.randomUUID()),
                createdAt = LocalDateTime.now(),
                todoItems = emptyList(),
            )
            val loadingState = ScreenLoadingState.NO_ERROR_INITIALIZED
            val newVmState = vmState.copy(
                loadingState = loadingState,
                basicState = vmState.basicState.updateInitialize(loadingState),
                isInCreating = true,
                workId = workId,
                formData = formData,
                validateExceptions = validateFormData(formData, vmState.isInShowEditingTodoForm),
            )

            updateVMState(newVmState)
            // 作成中なのでDBなど見に行く必要はなし
            return
        }

        // 編集中
        vmState = vmState.copy(
            basicState = vmState.basicState.toDoingAction(),
            isInCreating = false,
            workId = workId,
        )
        updateVMState(vmState)

        // DBデータでFormの初期化をしたときに初期化を完了とする
        viewModelScope.launch {
            // 編集用のフォームデータ
            val workResult = getWorkUseCase.call(GetWorkInput(workId))
            if (!workResult.isPresent) {
                val loadingState = ScreenLoadingState.NOT_FOUND_EXCEPTION
                updateVMState(
                    readVMState().copy(
                        loadingState = loadingState,
                        basicState = vmState.basicState.updateInitialize(loadingState),
                    )
                )

                return@launch
            }

            val formData = WorkFormData.fromDomainModel(workResult.get())
            val loadingState = ScreenLoadingState.NO_ERROR_INITIALIZED
            updateVMState(
                readVMState().copy(
                    loadingState = loadingState,
                    basicState = vmState.basicState.updateInitialize(loadingState),
                    formData = formData,
                    validateExceptions = validateFormData(
                        formData, readVMState().isInShowEditingTodoForm
                    ),
                )
            )
        }
    }

    override fun reloadVM() {
        reloadVMWithOptional(readVMState())
    }

    /**
     * アクションの実行結果を消費しつつVMをリロードする
     */
    override fun reloadVMWithConsumeActionDoneResult() {
        reloadVMWithOptional(readVMState()) { it.toConsumeActionDoneResult() }
    }

    /**
     * 必要なら追加アクションを対応しつつVMをリロードする
     */
    private fun reloadVMWithOptional(
        vmState: WorkEditVMState,
        optionalUpdater: (basicState: BasicScreenState) -> BasicScreenState = { it }
    ) {
        if (readVMState().loadingState == ScreenLoadingState.READY) {
            // 初期化中相当の時は無視する
            return
        }

        val loadingState = ScreenLoadingState.READY
        updateVMState(
            vmState.copy(
                loadingState = loadingState,
                basicState = optionalUpdater(vmState.basicState.updateInitialize(loadingState)),
            )
        )

        // 初期化をそのまま呼び出す
        initializeVM()
    }

    override fun replaceVMBasicScreenState(
        viewModelState: WorkEditVMState,
        basicScreenState: BasicScreenState
    ): WorkEditVMState {
        return viewModelState.copy(basicState = basicScreenState)
    }

    /**
     * タスク作成中かどうかを判定
     */
    private fun isInCreating(workId: Long): Boolean = workId == AsCreate.CREATING_ID

    /**
     * FormDataの完了状態を更新
     */
    fun updateWorkFormCompleted(isCompleted: Boolean) {
        val vmState = readVMState()
        val oldFormData = vmState.formData
        val newFormData = oldFormData.copy(
            completedAt = if (isCompleted) oldFormData.completedAt
                ?: LocalDateTime.now() else null,
        )
        val newVmState = vmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, vmState.isInShowEditingTodoForm),
        )

        updateVMState(newVmState)
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
            val newVmState = vmState.copy(
                isInShowEditingTodoForm = false,
                validateExceptions = validateFormData(vmState.formData, false),
            )
            updateVMState(newVmState)
            return
        }

        val vmState = readVMState()
        val newFormData = vmState.formData.copy(
            editingTodoItem = searchWorkTodoFormByUuid(uuid) ?: WorkTodoFormData.empty(uuid)
        )
        val newVmState = vmState.copy(
            isInShowEditingTodoForm = true,
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, true),
        )
        updateVMState(newVmState)
    }

    /**
     * 指定されたUUIDのWorkTodoFormデータを削除
     */
    fun removeWorkTodoForm(uuid: UUID) {
        val vmState = readVMState()
        val newWorkTodoForms: List<WorkTodoFormData> =
            vmState.formData.todoItems.toMutableList().filter { it.uuid != uuid }

        val newFormData = vmState.formData.copy(
            todoItems = newWorkTodoForms,
        )
        val newVmState = vmState.copy(
            formData = newFormData,
            validateExceptions = validateFormData(newFormData, vmState.isInShowEditingTodoForm),
        )
        updateVMState(newVmState)
    }

    /**
     * 指定された位置にあるWorkTodoFormデータを入れ替える
     */
    fun swapWorkTodoItem(fromIndex: Int, toIndex: Int) {
        val vmState = readVMState()
        val newWorkTodoForms: List<WorkTodoFormData> =
            vmState.formData.todoItems.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }
        val newFormData = vmState.formData.copy(
            todoItems = newWorkTodoForms,
        )
        val newVmState = vmState.copy(
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
        val oldWorkTodoFormData = vmState.formData.editingTodoItem
        val newWorkTodoFormData = oldWorkTodoFormData.copy(
            completedAt = if (isCompleted) oldWorkTodoFormData.completedAt
                ?: LocalDateTime.now() else null,
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
            isInShowEditingTodoForm = false, formData = vmState.formData.copy(
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
        formData: WorkFormData, isInShowEditingTodoForm: Boolean
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
                    // 未指定の場合は画面で設定できる範囲の時間と分を最小値に設定する
                    formData.beganTime ?: LocalTime.of(0, 0, 0)
                ) >= LocalDateTime.of(formData.endedDate, formData.endedTime ?: LocalTime.MAX)
            ) {
                ValidationException.of(
                    ValidationExceptionType.NeedSmallerThanDatetime(
                        LocalDateTime.of(
                            formData.endedDate,
                            // 未指定の場合は画面で設定できる範囲の時間と分を最大値に設定する
                            formData.endedTime ?: LocalTime.of(23, 59, 0)
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
                    formData.beganDate, formData.beganTime ?: LocalTime.MIN
                ) >= LocalDateTime.of(formData.endedDate, formData.endedTime ?: LocalTime.MAX)
            ) {
                ValidationException.of(
                    ValidationExceptionType.NeedBiggerThanDatetime(
                        LocalDateTime.of(
                            formData.beganDate, formData.beganTime ?: LocalTime.MIN
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
            isCompleted = ValidationException.empty(),
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
        if (!vmState.canLaunchAction() || vmState.validateExceptions.hasError()) {
            // エラーがある場合は作成や更新の処理を行わずに終了
            return
        }

        val newVmState = vmState.copy(basicState = vmState.basicState.toDoingAction())
        updateVMState(newVmState)

        viewModelScope.launch {
            // アプリを強制停止する場合を除いて、実行中に画面をpopしたりしたときなどに最後まで実行されないのは困る
            val result: Result<Unit> = async(Dispatchers.IO + SupervisorJob()) {
                if (isInCreating(vmState.workId)) {
                    return@async createWorkUseCase.call(CreateWorkInput(vmState.formData.toDomainModel()))
                } else {
                    return@async updateWorkUseCase.call(UpdateWorkInput(vmState.formData.toDomainModel()))
                }
            }.await()

            val oldVmState = readVMState()
            val actionDoneResult =
                if (result.isSuccess) (if (oldVmState.isInCreating) ActionDoneResult.SUCCEEDED_CREATE else ActionDoneResult.SUCCEEDED_UPDATE)
                else (if (oldVmState.isInCreating) ActionDoneResult.FAILED_CREATE else ActionDoneResult.FAILED_UPDATE)
            var newBasicState = oldVmState.basicState
                .toDoneAction()
                .toAcceptActionDoneResult(actionDoneResult)
            if (result.isSuccess) {
                newBasicState = newBasicState
                    // 強制アップデートを要求する
                    .toRequestForceUpdate()
            }
            // 実行結果を通知
            updateVMState(
                oldVmState.copy(basicState = newBasicState)
            )

            val doneWorkEvent =
                if (result.isSuccess) (if (oldVmState.isInCreating) SucceededCreateWork(workId = oldVmState.workId) else SucceededUpdateWork(
                    workId = oldVmState.workId
                ))
                else (if (oldVmState.isInCreating) FailedCreateWork(workId = oldVmState.workId) else FailedUpdateWork(
                    workId = oldVmState.workId
                ))
            EventBus.post(doneWorkEvent)
        }
    }

    /**
     * フォームを操作可能な状態にする
     */
    fun updateToEditingFormState() {
        val vmState = readVMState()
        val newVmState = vmState.copy(basicState = vmState.basicState.toDoneAction())
        updateVMState(newVmState)
    }
}