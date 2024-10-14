package com.ogata_k.mobile.winp.presentation.page.work.detail

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateInput
import com.ogata_k.mobile.winp.presentation.constant.DummyID
import com.ogata_k.mobile.winp.presentation.enumerate.ActionDoneResult
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.event.EventBus
import com.ogata_k.mobile.winp.presentation.event.work.FailedDeleteWork
import com.ogata_k.mobile.winp.presentation.event.work.FailedUpdateWork
import com.ogata_k.mobile.winp.presentation.event.work.FailedUpdateWorkTodo
import com.ogata_k.mobile.winp.presentation.event.work.SucceededDeleteWork
import com.ogata_k.mobile.winp.presentation.event.work.SucceededUpdateWork
import com.ogata_k.mobile.winp.presentation.event.work.SucceededUpdateWorkTodo
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.model.work.WorkTodo
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class WorkDetailVM @Inject constructor(
    private val getWorkUseCase: GetWorkAsyncUseCase,
    private val deleteWorkUseCase: DeleteWorkAsyncUseCase,
    private val updateWorkTodoStateUseCase: UpdateWorkTodoStateAsyncUseCase,
) : AbstractViewModel<ScreenLoadingState, WorkDetailVMState, ScreenLoadingState, WorkDetailUiState>() {
    override val viewModelStateFlow: MutableStateFlow<WorkDetailVMState> = MutableStateFlow(
        WorkDetailVMState(
            // 初期状態は未初期化状態とする
            loadingState = ScreenLoadingState.READY,
            basicState = BasicScreenState.initialState(),
            workId = DummyID.INVALID_ID,
            work = Optional.empty(),
            inShowMoreAction = false,
            inConfirmDelete = false,
            inConfirmWorkTodoState = null,
        )
    )

    override val uiStateFlow: StateFlow<WorkDetailUiState> =
        asUIStateFlow(viewModelScope, viewModelStateFlow)

    /**
     * workIdを初期化
     */
    fun setWorkId(workId: Int) {
        val vmState = readVMState()
        updateVMState(vmState.copy(workId = workId))
    }

    override fun initializeVM() {
        val vmState = readVMState()
        if (vmState.loadingState.isInitialized()) {
            // 初期化済みなので追加対応の必要なし
            return
        }
        // 事前にsetWorkId()で設定しておく必要がある
        val workId = vmState.workId

        if (workId == DummyID.INVALID_ID) {
            // workId不正
            val loadingState = ScreenLoadingState.ERROR
            updateVMState(
                vmState.copy(
                    loadingState = loadingState,
                    basicState = vmState.basicState.updateInitialize(loadingState),
                    work = Optional.empty(),
                )
            )
        } else {
            // DBデータでFormの初期化をしたときに初期化を完了とする
            viewModelScope.launch {
                val workResult = getWorkUseCase.call(GetWorkInput(workId))
                if (workResult.isFailure) {
                    val loadingState = ScreenLoadingState.NOT_FOUND_EXCEPTION
                    updateVMState(
                        readVMState().copy(
                            loadingState = loadingState,
                            basicState = vmState.basicState.updateInitialize(loadingState),
                            work = Optional.empty(),
                        )
                    )

                    return@launch
                }

                val loadingState = ScreenLoadingState.NO_ERROR_INITIALIZED
                updateVMState(
                    readVMState().copy(
                        loadingState = loadingState,
                        basicState = vmState.basicState.updateInitialize(loadingState),
                        work = Optional.of(Work.fromDomainModel(workResult.getOrThrow())),
                    )
                )
            }
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
        vmState: WorkDetailVMState,
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
        viewModelState: WorkDetailVMState,
        basicScreenState: BasicScreenState
    ): WorkDetailVMState {
        return viewModelState.copy(basicState = basicScreenState)
    }

    /**
     * Eventの監視
     */
    fun listenEvent(
        screenLifecycle: LifecycleOwner,
    ) {
        // 詳細で表示しているWorkはすでに作成済みなので作成イベントの監視は不要

        EventBus.onEvent<SucceededUpdateWork>(screenLifecycle) {
            val vmState = readVMState()
            if (it.workId != vmState.workId) {
                return@onEvent
            }
            reloadVM()
        }
        EventBus.onEvent<FailedUpdateWork>(screenLifecycle) {
            val vmState = readVMState()
            if (it.workId != vmState.workId) {
                return@onEvent
            }
            reloadVM()
        }

        // 削除処理はこの画面上で行うのでvmState.actionDoneResultsで直接管理する
    }

    /**
     * さらなる操作を要求するための操作一覧を表示するかどうかを切り替える
     */
    fun showMoreAction(show: Boolean = true) {
        val vmState = readVMState()
        if (vmState.inShowMoreAction || !show) {
            // 表示中なら非表示にする
            updateVMState(
                vmState.copy(
                    inShowMoreAction = show,
                )
            )
            return
        }

        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                inShowMoreAction = true,
            )
        )
    }

    /**
     * 削除をするかどうかの確認ダイアログを表示する
     */
    fun showDeleteConfirmDialog(show: Boolean = true) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                inConfirmDelete = show,
            )
        )
    }

    /**
     * 表示しているタスクを削除
     */
    fun deleteWork() {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            val actionDoneResult = ActionDoneResult.FAILED_DELETE

            // 正常に初期化ができていないなら削除はできないので失敗として通知
            updateVMState(
                vmState.copy(
                    basicState = vmState.basicState
                        .toDoneAction()
                        .toAcceptActionDoneResult(actionDoneResult),
                    // 削除確認中ダイアログを非表示にしつつscreenStateを更新する
                    inConfirmDelete = false,
                )
            )

            viewModelScope.launch {
                val doneWorkEvent = FailedDeleteWork(workId = vmState.workId)
                EventBus.post(doneWorkEvent)
            }
            return
        }

        val newVmState = vmState.copy(basicState = vmState.basicState.toDoingAction())
        updateVMState(newVmState)

        viewModelScope.launch {
            // アプリを強制停止する場合を除いて、実行中に画面をpopしたりしたときなどに最後まで実行されないのは困る
            val result: Result<Unit> = async(Dispatchers.IO + SupervisorJob()) {
                return@async deleteWorkUseCase.call(
                    DeleteWorkInput(
                        vmState.work.get().toDomainModel()
                    )
                )
            }.await()

            val oldVmState = readVMState()
            val actionDoneResult =
                if (result.isSuccess) ActionDoneResult.SUCCEEDED_DELETE else ActionDoneResult.FAILED_DELETE
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
                oldVmState.copy(
                    basicState = newBasicState,
                    // 削除確認中ダイアログを非表示にする
                    inConfirmDelete = false,
                )
            )

            val doneWorkEvent =
                if (result.isSuccess) SucceededDeleteWork(workId = oldVmState.workId)
                else FailedDeleteWork(workId = oldVmState.workId)
            EventBus.post(doneWorkEvent)
        }
    }

    /**
     * 指定したタスクのTODOの状態を更新するかどうかの確認ダイアログを表示する
     */
    fun showWorkTodoStateConfirmDialog(show: Int? = null) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                inConfirmWorkTodoState = show,
            )
        )
    }

    /**
     * 表示しているタスクTODOの状態を更新（完了しているなら完了していない状態にし、完了していないなら完了している状態にする）
     */
    fun updateWorkTodoState() {
        val vmState = readVMState()
        val todoItem: WorkTodo? = vmState.inConfirmWorkTodoState?.let {
            vmState.work.getOrNull()?.todoItems?.firstOrNull { it.id == vmState.inConfirmWorkTodoState }
        }

        if (!vmState.loadingState.isNoErrorInitialized() || todoItem == null) {
            val actionDoneResult = ActionDoneResult.FAILED_UPDATE

            // 正常に初期化ができていなかったり対象が見つからないのはフローとしておかしいのでエラーとして通知
            updateVMState(
                vmState.copy(
                    basicState = vmState.basicState
                        .toDoneAction()
                        .toAcceptActionDoneResult(actionDoneResult),
                    inConfirmWorkTodoState = null,
                )
            )

            viewModelScope.launch {
                val doneWorkEvent =
                    FailedUpdateWorkTodo(workId = vmState.workId, workTodoId = todoItem?.id)
                EventBus.post(doneWorkEvent)
            }
            return
        }

        val newVmState = vmState.copy(basicState = vmState.basicState.toDoingAction())
        updateVMState(newVmState)

        viewModelScope.launch {
            // アプリを強制停止する場合を除いて、実行中に画面をpopしたりしたときなどに最後まで実行されないのは困る
            val result: Result<Work> = async(Dispatchers.IO + SupervisorJob()) {
                return@async updateWorkTodoStateUseCase.call(
                    UpdateWorkTodoStateInput(
                        vmState.work.get().toDomainModel(),
                        todoItem.id,
                    )
                ).map { Work.fromDomainModel(it) }
            }.await()

            val oldVmState = readVMState()
            val actionDoneResult =
                if (result.isSuccess) ActionDoneResult.SUCCEEDED_UPDATE else ActionDoneResult.FAILED_UPDATE
            val newBasicState = oldVmState.basicState
                .toDoneAction()
                .toAcceptActionDoneResult(actionDoneResult)
            // 実行結果を通知
            updateVMState(
                oldVmState.copy(
                    // 強制アップデートは要求しない
                    basicState = newBasicState,
                    // ダイアログを非表示にする
                    inConfirmWorkTodoState = null,
                    work = if (result.isSuccess) Optional.of(result.getOrThrow()) else oldVmState.work,
                )
            )

            val doneWorkEvent =
                if (result.isSuccess) SucceededUpdateWorkTodo(
                    workId = oldVmState.workId,
                    workTodoId = todoItem.id
                )
                else FailedUpdateWorkTodo(workId = oldVmState.workId, workTodoId = todoItem.id)
            EventBus.post(doneWorkEvent)
        }
    }
}