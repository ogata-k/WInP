package com.ogata_k.mobile.winp.presentation.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    initialViewModelState: ViewModelState
) : ViewModel() {
    private val _viewModelState: MutableStateFlow<ViewModelState> =
        MutableStateFlow(initialViewModelState)

    protected fun readState(): ViewModelState {
        return _viewModelState.value
    }

    protected fun updateState(newViewModelState: ViewModelState) {
        _viewModelState.value = newViewModelState
    }

    val uiState: StateFlow<UiState> = _viewModelState
        .map { v -> v.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _viewModelState.value.toUiState())
}