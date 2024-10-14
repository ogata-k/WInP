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
     * アクションの実行結果を消費せずに先頭を覗き見る
     */
    fun peekActionDoneResult(): ActionDoneResult? {
        return basicState.actionDoneResults.firstOrNull()
    }
}