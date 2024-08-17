package com.ogata_k.mobile.winp.presentation.enumerate

/**
 * UIの初期化状態を表すEnum
 */
enum class UiInitializeState {
    LOADING,
    INITIALIZED,
    ERROR;

    /**
     * 初期化済みの状態ならtrue
     */
    fun isInitialized(): Boolean {
        return this == INITIALIZED || this == ERROR
    }

    /**
     * エラーがあるならtrue
     */
    fun hasError(): Boolean {
        return this == ERROR
    }
}