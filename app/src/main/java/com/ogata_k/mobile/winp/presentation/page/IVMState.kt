package com.ogata_k.mobile.winp.presentation.page

import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState

/**
 * 画面の読み込み状態。主に初期化されたかの状態の管理に使う。
 *
 * ※初期化されたかの状態を管理するときにEnumを使うときは[ScreenLoadingState]を使う。
 * Enumを使わずにSealedクラスで管理するときはこのインターフェースを継承したクラスを使うことを想定している。
 */
interface IScreenLoadingState {
    /**
     * 初期化待ちの状態ならtrue
     */
    fun isInReady(): Boolean

    /**
     * 初期化済みの状態ならtrue
     */
    fun isInitialized(): Boolean

    /**
     * エラーなしでの初期化済みの状態ならtrue
     */
    fun isNoErrorInitialized(): Boolean

    /**
     * エラーがあるならtrue
     */
    fun isInError(): Boolean

    /**
     * アクションを実行可能か
     */
    fun canLaunchAction(basicScreenState: BasicScreenState): Boolean {
        return basicScreenState.actionState.canLaunch() && isInitialized()
    }
}

/**
 * VMStateが保持していてほしい処理一覧
 */
interface IVMState<VMLoadingState : IScreenLoadingState, UiLoadingState : IScreenLoadingState, UiState : IUiState<UiLoadingState>> {
    val loadingState: VMLoadingState
    val basicState: BasicScreenState

    /**
     * UiStateに変換する
     */
    fun toUiState(): UiState

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
    fun peekSnackbarEvent(): SnackbarEvent? {
        return basicState.snackbarEvents.firstOrNull()
    }
}

/**
 * VMStateをハンドリング可能なクラスが必須とする処理
 */
interface IVMStateHandler {
    /**
     * 初期化
     */
    fun initializeVM()

    /**
     * 画面のリロードを行い、[BasicScreenState.needForceUpdate]をfalseにする
     */
    fun reloadVM()

    /**
     * アクションの実行結果を消費しつつ画面のリロードを行う
     */
    fun reloadVMWithConsumeEvent()

    /**
     * 画面のリロードを要求するために[BasicScreenState.toRequestForceUpdate]を実行して状態を更新する
     */
    fun requestForceUpdate()

    /**
     * アクションの実行結果を受信する
     */
    fun acceptSnackbarEvent(snackbarEvent: SnackbarEvent)

    /**
     * アクションの実行結果を使ったので先頭から取り除く
     */
    fun consumeEvent()
}