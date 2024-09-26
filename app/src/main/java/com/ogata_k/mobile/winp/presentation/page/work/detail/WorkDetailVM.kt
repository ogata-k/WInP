package com.ogata_k.mobile.winp.presentation.page.work.detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkInput
import com.ogata_k.mobile.winp.presentation.constant.DummyID
import com.ogata_k.mobile.winp.presentation.enumerate.UiInitializeState
import com.ogata_k.mobile.winp.presentation.enumerate.UiNextScreenState
import com.ogata_k.mobile.winp.presentation.model.common.UiLoadingState
import com.ogata_k.mobile.winp.presentation.model.work.Work
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

@HiltViewModel
class WorkDetailVM @Inject constructor(
    private val getWorkUseCase: GetWorkAsyncUseCase,
    private val deleteWorkUseCase: DeleteWorkAsyncUseCase,
) : AbstractViewModel<WorkDetailVMState, WorkDetailUiState>() {
    override val viewModelStateFlow: MutableStateFlow<WorkDetailVMState> = MutableStateFlow(
        WorkDetailVMState(
            // 初期状態は未初期化状態とする
            uiLoadingState = UiLoadingState.initialState(),
            workId = DummyID.INVALID_ID,
            work = Optional.empty(),
            inShowMoreAction = false,
            inConfirmDelete = false,
        )
    )
    override val uiStateFlow: StateFlow<WorkDetailUiState> =
        asUIStateFlow(viewModelScope, viewModelStateFlow)

    /**
     * 初期化に失敗した
     */
    fun failInitializeByInvalidWorkId() {
        Log.e(javaClass.name, "Invalid WorkId")
        val vmState = readVMState()
        updateVMState(
            vmState.copy(
                uiLoadingState = vmState.uiLoadingState.toInitializedWithRuntimeException(),
                work = Optional.empty(),
            )
        )
    }

    /**
     * 初期化
     */
    fun initialize(workId: Int) {
        val vmState = readVMState()
        if (vmState.uiLoadingState.initializeState.isInitialized()) {
            // 初期化済みなので追加対応の必要なし
            return
        }

        val newVmState = vmState.copy(workId = workId)
        updateVMState(newVmState)

        // DBデータでFormの初期化をしたときに初期化を完了とする
        viewModelScope.launch {
            // 編集用のフォームデータ
            val workResult = getWorkUseCase.call(GetWorkInput(workId))
            if (workResult.isFailure) {
                updateVMState(
                    readVMState().copy(
                        uiLoadingState = readVMState().uiLoadingState.toInitializedWithNotFoundException(),
                        work = Optional.empty(),
                    )
                )

                return@launch
            }

            updateVMState(
                readVMState().copy(
                    uiLoadingState = readVMState().uiLoadingState.toInitialized(),
                    work = Optional.of(Work.fromDomainModel(workResult.getOrThrow())),
                )
            )
        }
    }

    /**
     * 編集画面などの次の画面の状態を受けて保持している値を更新する
     */
    fun updateNextScreenStateWithReloadLaunch(nextScreenState: UiNextScreenState) {
        if (!nextScreenState.isDoneAction() && !readVMState().uiLoadingState.formState.canDoAction()) {
            // 実行したとみなせないなら何もせずに終了
            return
        }

        val vmState = readVMState()
            .copy(
                uiLoadingState = readVMState().uiLoadingState.toStartReloadStateBy(nextScreenState),
            )
        updateVMState(vmState)

        viewModelScope.launch {
            // 再度タスクデータを取得して更新を行う
            val workResult = getWorkUseCase.call(GetWorkInput(vmState.workId))
            if (workResult.isFailure) {
                updateVMState(
                    readVMState().copy(
                        // この画面から派生する編集画面などでデータが見つからないエラーになる可能性
                        // がある状態なのでscreenStateをエラーとして記録しておく
                        uiLoadingState = readVMState().uiLoadingState.toReloadedWithNotFoundException(),
                        work = Optional.empty(),
                    )
                )

                return@launch
            }

            updateVMState(
                readVMState().copy(
                    uiLoadingState = readVMState().uiLoadingState.toReloadedStateBy(nextScreenState),
                    work = Optional.of(Work.fromDomainModel(workResult.getOrThrow())),
                )
            )
        }
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

        if (vmState.uiLoadingState.initializeState != UiInitializeState.INITIALIZED || !vmState.work.isPresent) {
            // 初期化もしていないなら削除はできない
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
        if (vmState.uiLoadingState.initializeState != UiInitializeState.INITIALIZED || !vmState.work.isPresent) {
            // 初期化もしていないなら削除はできない
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
        if (vmState.uiLoadingState.initializeState != UiInitializeState.INITIALIZED || !vmState.work.isPresent) {
            // 初期化もしていないなら削除はできないので失敗として通知
            updateVMState(
                vmState.copy(
                    // 削除確認中ダイアログを非表示にしつつscreenStateを更新する
                    inConfirmDelete = false,
                    uiLoadingState = vmState.uiLoadingState.toDoneWithError(),
                )
            )
            return
        }

        val newVmState = vmState.copy(uiLoadingState = vmState.uiLoadingState.toDoingAction())
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

            // 実行結果を通知
            updateVMState(
                readVMState().copy(
                    // 削除確認中ダイアログを非表示にしつつscreenStateを更新する
                    inConfirmDelete = false,
                    uiLoadingState = if (result.isSuccess) readVMState().uiLoadingState.toDoneDelete() else readVMState().uiLoadingState.toDoneWithError(),
                )
            )
        }
    }

    /**
     * フォームを操作可能な状態にする
     */
    fun updateToEditingFormState() {
        val vmState = readVMState()
        val newVmState = vmState.copy(uiLoadingState = vmState.uiLoadingState.forceToUsingForm())
        updateVMState(newVmState)
    }
}