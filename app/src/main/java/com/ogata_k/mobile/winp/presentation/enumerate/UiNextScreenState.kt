package com.ogata_k.mobile.winp.presentation.enumerate

import androidx.navigation.NavController

/**
 * 次の画面の結果
 */
enum class UiNextScreenState {
    LOADING,
    INITIALIZED,
    CREATED,
    UPDATED,
    DELETED,
    NOT_FOUND_EXCEPTION,
    ERROR;

    companion object {
        const val KEY: String = "UiNextScreenStateKey"

        /**
         * 次の画面で設定されたUI最終状態を取得する。
         */
        fun takeState(navController: NavController, remove: Boolean = true): UiNextScreenState? {
            return if (remove) {
                navController.currentBackStackEntry?.savedStateHandle?.remove<UiNextScreenState>(
                    UiNextScreenState.KEY
                )
            } else {
                navController.currentBackStackEntry?.savedStateHandle?.get<UiNextScreenState>(
                    UiNextScreenState.KEY
                )
            }
        }
    }

    /**
     * 戻る先の画面に現在のUI最終状態を設定する
     */
    fun setState(navController: NavController) {
        navController.previousBackStackEntry?.savedStateHandle?.set(UiNextScreenState.KEY, this)
    }

    /**
     * 戻る先の画面に現在のUI最終状態を設定してから画面をPOPする。
     * インスタンスがNullの可能性がある場合はsetState()を実行してからnavController.popBackStack()したほうがわかりやすい
     */
    fun popWithSetState(navController: NavController) {
        setState(navController)
        navController.popBackStack()
    }

    /**
     * 初期化済みの状態ならtrue
     */
    fun isInitialized(): Boolean {
        return this != LOADING
    }

    /**
     * エラーがあるならtrue
     */
    fun isError(): Boolean {
        return this == ERROR || this == NOT_FOUND_EXCEPTION
    }

    /**
     * 次の画面のアクション実行結果で、現在の画面で何かしら更新の必要がありそうな値ならtrueを返す
     */
    fun isDoneAction(): Boolean {
        return arrayOf(
            CREATED,
            UPDATED,
            DELETED,
            NOT_FOUND_EXCEPTION
        ).contains(this)
    }
}