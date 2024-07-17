package com.ogata_k.mobile.winp.presentation.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * UIの状態に変換する方法を提供するインターフェース
 */
interface ToUiState<out UiState> {
    fun toUiState(): UiState
}

/**
 * ViewModelStateの状態を変更することでUiStateを自動的に更新することができるViewModel
 */
abstract class AbstractViewModel<ViewModelState : ToUiState<UiState>, UiState>(
    initialViewModelState: (viewModelScope: CoroutineScope) -> ViewModelState
) : ViewModel() {
    private val _viewModelState: MutableStateFlow<ViewModelState> =
        MutableStateFlow(initialViewModelState(viewModelScope))

    /**
     * 現在のViewModelの状態を取得
     */
    protected fun readVMState(): ViewModelState {
        return _viewModelState.value
    }

    /**
     * これを呼び出すことでUIも更新される
     */
    protected fun updateVMState(newViewModelState: ViewModelState) {
        _viewModelState.value = newViewModelState
    }

    /**
     * UI用の状態フロー
     */
    val uiState: StateFlow<UiState> = _viewModelState
        .map { v -> v.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _viewModelState.value.toUiState())
}