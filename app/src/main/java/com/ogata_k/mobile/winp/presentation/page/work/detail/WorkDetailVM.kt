package com.ogata_k.mobile.winp.presentation.page.work.detail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkInput
import com.ogata_k.mobile.winp.presentation.constant.DummyID
import com.ogata_k.mobile.winp.presentation.enumerate.UiInitializeState
import com.ogata_k.mobile.winp.presentation.enumerate.UiNextScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Optional
import javax.inject.Inject

@HiltViewModel
class WorkDetailVM @Inject constructor(
    private val getWorkUseCase: GetWorkAsyncUseCase,
) : AbstractViewModel<WorkDetailVMState, WorkDetailUiState>() {
    override val viewModelStateFlow: MutableStateFlow<WorkDetailVMState> = MutableStateFlow(
        WorkDetailVMState(
            // 初期状態は未初期化状態とする
            initializeState = UiInitializeState.LOADING,
            screenState = UiNextScreenState.LOADING,
            workId = DummyID.INVALID_ID,
            work = Optional.empty(),
        )
    )
    override val uiStateFlow: StateFlow<WorkDetailUiState> =
        asUIStateFlow(viewModelScope, viewModelStateFlow)

    /**
     * 初期化に失敗した
     */
    fun failInitializeByInvalidWorkId() {
        Log.e(javaClass.name, "Invalid WorkId")
        updateVMState(
            readVMState().copy(
                initializeState = UiInitializeState.ERROR,
                screenState = UiNextScreenState.ERROR,
                work = Optional.empty(),
            )
        )
    }

    /**
     * 初期化
     */
    fun initialize(workId: Int) {
        val vmState = readVMState()
        if (vmState.initializeState.isInitialized()) {
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
                        initializeState = UiInitializeState.NOT_FOUND_EXCEPTION,
                        screenState = UiNextScreenState.ERROR,
                        work = Optional.empty(),
                    )
                )

                return@launch
            }

            updateVMState(
                readVMState().copy(
                    initializeState = UiInitializeState.INITIALIZED,
                    screenState = UiNextScreenState.INITIALIZED,
                    work = Optional.of(Work.fromDomainModel(workResult.getOrThrow())),
                )
            )
        }
    }

    /**
     * 編集画面などの次の画面の状態を受けて保持している値を更新する
     */
    fun updateNextScreenState(nextScreenState: UiNextScreenState) {
        updateVMState(readVMState().copy(screenState = nextScreenState))
    }
}