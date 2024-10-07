package com.ogata_k.mobile.winp.presentation.enumerate

import com.ogata_k.mobile.winp.presentation.page.IScreenLoadingState

/**
 * UIの初期化状態を表すEnum
 */
enum class ScreenLoadingState : IScreenLoadingState {
    READY,
    NO_ERROR_INITIALIZED,
    NOT_FOUND_EXCEPTION,
    ERROR;

    /**
     * 初期化待ちの状態ならtrue
     */
    override fun isInReady(): Boolean {
        return this == READY
    }

    /**
     * 初期化済みの状態ならtrue
     */
    override fun isInitialized(): Boolean {
        return this != READY
    }

    /**
     * エラーなしでの初期化済みの状態ならtrue
     */
    override fun isNoErrorInitialized(): Boolean {
        return this == NO_ERROR_INITIALIZED
    }

    /**
     * エラーがあるならtrue
     */
    override fun isInError(): Boolean {
        return this == ERROR || this == NOT_FOUND_EXCEPTION
    }
}