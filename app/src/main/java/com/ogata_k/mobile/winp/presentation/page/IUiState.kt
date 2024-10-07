package com.ogata_k.mobile.winp.presentation.page

import com.ogata_k.mobile.winp.presentation.enumerate.ActionDoneResult
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState

/**
 * UiStateが保持していてほしい処理一覧
 */
interface IUiState<UiScreenLoadingState : IScreenLoadingState> {
    val loadingState: UiScreenLoadingState
    val basicState: BasicScreenState

    /**
     * 初期化中の状態ならtrue
     */
    fun isInitialLoading(): Boolean {
        return loadingState.isInReady()
    }

    /**
     * 初期化済みの状態ならtrue
     */
    fun isInitialized(): Boolean {
        return loadingState.isInitialized()
    }

    /**
     * エラーがあるならtrue
     */
    fun isInError(): Boolean {
        return loadingState.isInError()
    }

    /**
     * アクションを実行可能か
     */
    fun canLaunchAction(): Boolean {
        return loadingState.canLaunchAction(basicState)
    }

    /**
     * アクションの実行結果を消費せずに先頭を覗き見る
     */
    fun peekActionDoneResult(): ActionDoneResult? {
        return basicState.actionDoneResults.firstOrNull()
    }
}