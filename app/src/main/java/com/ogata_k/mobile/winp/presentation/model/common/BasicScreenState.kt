package com.ogata_k.mobile.winp.presentation.model.common

import com.ogata_k.mobile.winp.presentation.enumerate.ActionDoneResult
import com.ogata_k.mobile.winp.presentation.enumerate.LaunchActionState
import com.ogata_k.mobile.winp.presentation.page.IScreenLoadingState

/**
 * 基本的な画面状態
 */
data class BasicScreenState(
    /**
     * アクションを実行できるかどうかを管理
     */
    val actionState: LaunchActionState,

    /**
     * 画面の再読込が必要ならtrue
     * なくても何とかなりそうだが、処理を完了して画面を描画するために使う
     */
    val needForceUpdate: Boolean,

    /**
     * 画面上で実行した実行結果一覧を保持する。
     * 想定では一つずつ取り出して、画面を跨ぐならトースト表示、画面を跨がないならSnackBarで表示。
     */
    val actionDoneResults: List<ActionDoneResult>,
) {
    companion object {
        /**
         * 初期状態
         */
        fun initialState(): BasicScreenState {
            return BasicScreenState(
                actionState = LaunchActionState.CANNOT_LAUNCH,
                needForceUpdate = false,
                actionDoneResults = listOf(),
            )
        }
    }

    /**
     * 初期化状態を更新
     */
    fun updateInitialize(screenLoadingState: IScreenLoadingState): BasicScreenState {
        if (screenLoadingState.isInReady()) {
            // 初期化中の状態なので初期化状態にする
            return copy(
                actionState = LaunchActionState.CANNOT_LAUNCH,
                needForceUpdate = false,
                // 実行結果は追加されていた可能性があるので以前のものを使う
            )
        }

        if (screenLoadingState.isInitialized()) {
            // 初期化成功
            return copy(
                actionState = LaunchActionState.CAN_LAUNCH,
                needForceUpdate = false,
                // 実行結果は追加されていた可能性があるので以前のものを使う
            )
        }

        // 初期化中でも初期化成功でもないので初期化失敗とする
        return copy(
            actionState = LaunchActionState.CANNOT_LAUNCH,
            needForceUpdate = false,
            // 実行結果は追加されていた可能性があるので以前のものを使う
        )
    }

    /**
     * アクションを実行する
     */
    fun toDoingAction(): BasicScreenState {
        return copy(actionState = LaunchActionState.DOING)
    }

    /**
     * アクションを実行する
     */
    fun toDoneAction(): BasicScreenState {
        return copy(actionState = LaunchActionState.CAN_LAUNCH)
    }

    /**
     * 画面の強制更新（リロード）のリクエストが必要な状態にする。
     * ※これによってリロードを受理した後は、needForceUpdateScreen = falseにする必要あり
     */
    fun toRequestForceUpdate(): BasicScreenState {
        return copy(needForceUpdate = true)
    }

    /**
     * アクションの実行結果を受信した状態にする
     */
    fun toAcceptActionDoneResult(actionResult: ActionDoneResult): BasicScreenState {
        val newActionDoneResults = actionDoneResults.toMutableList()
        newActionDoneResults.add(actionResult)
        return copy(actionDoneResults = newActionDoneResults)
    }

    /**
     * アクションの実行結果を使ったので先頭から取り除く
     */
    fun toConsumeActionDoneResult(): BasicScreenState {
        val newActionDoneResults = actionDoneResults.toMutableList()
        newActionDoneResults.removeFirstOrNull()
        return copy(actionDoneResults = newActionDoneResults)
    }
}
