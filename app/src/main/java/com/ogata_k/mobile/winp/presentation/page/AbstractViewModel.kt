package com.ogata_k.mobile.winp.presentation.page

import androidx.lifecycle.ViewModel
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

abstract class AbstractViewModel<VMLoadingState : IScreenLoadingState, ViewModelState : IVMState<VMLoadingState, UiLoadingState, UiState>, UiLoadingState : IScreenLoadingState, UiState : IUiState<UiLoadingState>> :
    ViewModel(), IVMStateHandler {
    companion object {
        @JvmStatic
        protected fun <VMLoadingState : IScreenLoadingState, ViewModelState : IVMState<VMLoadingState, UiLoadingState, UiState>, UiLoadingState : IScreenLoadingState, UiState : IUiState<UiLoadingState>> asUIStateFlow(
            viewModelScope: CoroutineScope,
            viewModelStateFlow: MutableStateFlow<ViewModelState>
        ): StateFlow<UiState> {
            return viewModelStateFlow.map { v -> v.toUiState() }
                .stateIn(
                    viewModelScope,
                    SharingStarted.Eagerly,
                    viewModelStateFlow.value.toUiState()
                )
        }
    }

    /**
     * ViewModel用の状態フロー
     */
    protected abstract val viewModelStateFlow: MutableStateFlow<ViewModelState>

    /**
     * UI用の状態フロー
     */
    abstract val uiStateFlow: StateFlow<UiState>

    /**
     * 現在のViewModelの状態を取得
     */
    protected fun readVMState(): ViewModelState {
        return viewModelStateFlow.value
    }

    /**
     * これを呼び出すことでUIも更新される
     */
    protected fun updateVMState(newViewModelState: ViewModelState) {
        viewModelStateFlow.value = newViewModelState
    }

    /**
     * VMStateの[BasicScreenState]を上書きしたものを返す。共通処理で利用する想定。
     */
    protected abstract fun replaceVMBasicScreenState(
        viewModelState: ViewModelState,
        basicScreenState: BasicScreenState
    ): ViewModelState

    /**
     * 画面のリロードを要求するために[BasicScreenState.toRequestForceUpdate]を実行して状態を更新する
     */
    override fun requestForceUpdate() {
        val vmState = readVMState()
        updateVMState(
            replaceVMBasicScreenState(
                vmState,
                vmState.basicState.toRequestForceUpdate()
            )
        )
    }

    /**
     * アクションの実行結果を受信する
     */
    override fun acceptSnackbarEvent(snackbarEvent: SnackbarEvent) {
        val vmState = readVMState()
        updateVMState(
            replaceVMBasicScreenState(
                vmState,
                vmState.basicState.toAcceptSnackbarEvent(snackbarEvent)
            )
        )
    }

    /**
     * アクションの実行結果を使ったので先頭から取り除く
     */
    override fun consumeEvent() {
        val vmState = readVMState()
        updateVMState(
            replaceVMBasicScreenState(
                vmState,
                vmState.basicState.toConsumeSnackbarEvent()
            )
        )
    }
}